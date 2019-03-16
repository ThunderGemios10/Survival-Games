package com.thundergemios10.survivalgames.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.SurvivalGames;




public class QueueManager {

	private static QueueManager instance = new QueueManager();
	private Hashtable<Integer, ArrayList<BlockData>> queue = new Hashtable<Integer, ArrayList<BlockData>>();
	File baseDir;

	private QueueManager(){

	}

	public static QueueManager getInstance(){
		return instance;
	}

	public void setup(){
		baseDir = new File(SurvivalGames.getPluginDataFolder()+"/ArenaData/");
		try{
			if(!baseDir.exists()){
				baseDir.mkdirs();
			}
			for(Game g : GameManager.getInstance().getGames()){
				ensureFile(g.getID());
			}

		}catch(Exception e){}

		Bukkit.getScheduler().runTaskTimerAsynchronously(GameManager.getInstance().getPlugin(), new DataDumper(), 100, 100);
	}



	public void rollback(final int id, final boolean shutdown){
		loadSave(id);
		if(!shutdown){
			Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(),
					new Rollback(id, shutdown,0,1,0));
		}
		else{
			new Rollback(id, shutdown,0,1,0).run();
		}

		if(shutdown){
			new ResetChests(id).run();
		}
		else{ 
			Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), 
					new ResetChests(id));
		}

		if(shutdown){
			new RemoveEntities(id).run();
		}
		else{ 
			Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), 
					new RemoveEntities(id), 5);
		}


	}
	
	public void restockChests(final int id) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), 
				new ResetChests(id));	
	}

	class RemoveEntities implements Runnable{
		private int id;

		protected RemoveEntities(int id){
			this.id = id;
		}

		@SuppressWarnings("deprecation")
		public void run(){
			ArrayList<Entity>removelist = new ArrayList<Entity>();
			ArrayList<String> keeplist =  new ArrayList<String>();

			keeplist.addAll(SettingsManager.getInstance().getConfig().getStringList("entities.keep"));
			
			for(Entity e:SettingsManager.getGameWorld(id).getEntities()){
				if((!(e instanceof Player)) && (!(e instanceof HumanEntity))){
					if( ! keeplist.contains(e.getType().getName())  ) {
						if(GameManager.getInstance().getBlockGameId(e.getLocation()) == id){
							removelist.add(e);
							SurvivalGames.debug("Removing an entity of type "+e.getType().getName());
						}
					}
				}
			}
			for(int a = 0; a < removelist.size(); a = 0){
				try{removelist.remove(0).remove();}catch(Exception e){}
			}
		}
	}


	public void add(BlockData data){
		ArrayList<BlockData>dat = queue.get(data.getGameId());
		if(dat == null){
			dat = new ArrayList<BlockData>();
			ensureFile(data.getGameId());
		}
		dat.add(data);
		queue.put(data.getGameId(), dat);

	}

	public void ensureFile(int id){
		try{
			File f2 = new File(baseDir, "Arena"+id+".dat");
			if(!f2.exists()){
				f2.createNewFile();
			}
		}catch(Exception e){}
	}

	class DataDumper implements Runnable{
		public void run(){
			for(int id: queue.keySet()){
				try{

					ArrayList<BlockData>data = queue.get(id);
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(baseDir, "Arena"+id+".dat")));

					out.writeObject(data);
					out.flush();
					out.close();

				}catch(Exception e){}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadSave(int id){
		ensureFile(id);
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(baseDir, "Arena"+id+".dat")));

			ArrayList<BlockData>input = (ArrayList<BlockData>) in.readObject();

			ArrayList<BlockData>data = queue.get(id); 
			if(data == null){
				data = new ArrayList<BlockData>();
			}

			for(BlockData d:input){
				if(!data.contains(d)){
					data.add(d);
				}
			}

			queue.put(id, data);
			in.close();
		}catch(Exception e){}
	}

	
	class ResetChests implements Runnable{
		private int id;

		protected ResetChests(int id){
			this.id = id;
		}

		public void run(){
			HashMap<Block,ItemStack[]>openedChests = GameManager.openedChest.get(id);
			if( openedChests == null ) { SurvivalGames.debug("Nothing to reset for id "+id); return; }
			SurvivalGames.debug("Resetting saved chests content for game "+id);
			for( Block chest: openedChests.keySet() ) {
				BlockState bs = chest.getState();
				if(bs instanceof Chest || bs instanceof DoubleChest){
					SurvivalGames.debug("Resetting chest at "+chest.getX()+","+chest.getY()+","+chest.getZ()+" to previous contents");
					Inventory inv = ((bs instanceof Chest))? ((Chest) bs).getBlockInventory()
					: ((DoubleChest)bs).getLeftSide().getInventory(); // should handle double chests correctly!
					// replace current contents with saved contents
					try {
						inv.setContents(openedChests.get(chest));
						if(SettingsManager.getInstance().getConfig().getBoolean("debug", false)) {
							for( ItemStack is: inv.getContents() ) {
								if( is != null ) {
									SurvivalGames.debug("Restored item "+ is.getType().name() + " DV " + is.getDurability() + " qty "+is.getAmount());
								}
							}
						}
					} catch(Exception e) {
					    SurvivalGames.warning("Problem resetting chest at " +chest.getX()+","+chest.getY()+","+chest.getZ()+" to original state!");
					}
				} else {
					SurvivalGames.warning("Block in saved chests map is no longer a chest?");
				}
			}
			// forget saved content, so that randomisation can occur
			SurvivalGames.debug("Emptying list of opened chests for game "+id);
			GameManager.openedChest.put(id, new HashMap < Block, ItemStack[] > ());
		}
	}


	class Rollback implements Runnable{

		int id, totalRollback, iteration;
		Game game;
		long time;

		boolean shutdown;

		public Rollback(int id, boolean shutdown, int trb, int it, long time){
			this.id = id;
			this.totalRollback = trb;
			this.iteration = it;
			this.time = time;
			this.game = GameManager.getInstance().getGame(id);
			this.shutdown = shutdown;
		}

		@SuppressWarnings("deprecation")
		public void run(){

			ArrayList<BlockData>data = queue.get(id);
			if(data != null){
				int a = data.size()-1;
				int rb = 0;
				long t1 = new Date().getTime();
				int pt = SettingsManager.getInstance().getConfig().getInt("rollback.per-tick", 100);
				while(a>=0 && (rb < pt|| shutdown)){
					// SurvivalGames.debug("Reseting "+a); // this makes a lot of noise in the logs
					BlockData result = data.get(a);
					if(result.getGameId() == this.game.getID()){

						data.remove(a);
						Location l = new Location(Bukkit.getWorld(result.getWorld()), result.getX(), result.getY(), result.getZ());
						Block b = l.getBlock();
						b.setTypeIdAndData(1, result.getPrevdata(), false);
						b.setType(result.getPrevType());
						b.getState().update();

						/*	if(result.getItems() != null){
							Chest c = (Chest)b;
							c.getBlockInventory().setContents(result.getItems());
						}
						 */

						rb++;

					}
					a--;
				}
				time += new Date().getTime() - t1;
				if(a != -1){
					Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(),
							new Rollback(id, shutdown, totalRollback + rb, iteration+1, time), 1);
				}
				else{
					SurvivalGames.info("Arena "+id+" reset. Rolled back "+(totalRollback+rb)+" blocks in "+iteration+" iterations ("+pt+" blocks per iteration Total time spent rolling back was "+time+"ms)");
					this.game.resetCallback();
				}
			}else{
				SurvivalGames.info("Arena "+id+" reset. Rolled back "+totalRollback+" blocks in "+iteration+" iterations. Total time spent rolling back was "+time+"ms");
				this.game.resetCallback();
			}
		}
	}
}


