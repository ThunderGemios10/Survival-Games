package com.thundergemios10.survivalgames;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.thundergemios10.survivalgames.Game.GameMode;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.stats.StatsManager;
import com.thundergemios10.survivalgames.util.Kit;
import com.thundergemios10.survivalgames.util.ReflectionUtils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class GameManager {

	static GameManager instance = new GameManager();
	private ArrayList < Game > games = new ArrayList < Game > ();
	private SurvivalGames p;
	public static HashMap < Integer, HashMap < Block, ItemStack[] >> openedChest = new HashMap < Integer, HashMap < Block, ItemStack[] >> ();
	private ArrayList<Kit>kits = new ArrayList<Kit>();
	private HashSet<Player>kitsel = new HashSet<Player>();
	MessageManager msgmgr = MessageManager.getInstance();
	

	
	private GameManager() {

	}

	public static GameManager getInstance() {
		return instance;
	}

	public void setup(SurvivalGames plugin) {
		p = plugin;
		LoadGames();
		LoadKits();
		for (Game g: getGames()) {
			openedChest.put(g.getID(), new HashMap < Block, ItemStack[] > ());
		}
	}

	public Plugin getPlugin() {
		return p;
	}

	public void reloadGames() {
		LoadGames();
	}


	public void LoadKits(){
		Set<String> kits1 = SettingsManager.getInstance().getKits().getConfigurationSection("kits").getKeys(false);
		for(String s:kits1){
			kits.add(new Kit(s));
		}
	}

	public void LoadGames() {
		FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
		games.clear();
		int no = c.getInt("sg-system.arenano", 0);
		int loaded = 0;
		int a = 1;
		while (loaded < no) {
			if (c.isSet("sg-system.arenas." + a + ".x1")) {
				//c.set("sg-system.arenas."+a+".enabled",c.getBoolean("sg-system.arena."+a+".enabled", true));
				if (c.getBoolean("sg-system.arenas." + a + ".enabled")) {
					//SurvivalGames.$(c.getString("sg-system.arenas."+a+".enabled"));
					//c.set("sg-system.arenas."+a+".vip",c.getBoolean("sg-system.arenas."+a+".vip", false));
					SurvivalGames.$("Loading Arena: " + a);
					loaded++;
					games.add(new Game(a));
					StatsManager.getInstance().addArena(a);
				}
			}
			a++;
			
		}
		LobbyManager.getInstance().clearAllSigns();
		
	}

	public int getBlockGameId(Location v) {
		for (Game g: games) {
			if (g.isBlockInArena(v)) {
				return g.getID();
			}
		}
		return -1;
	}

	public int getPlayerGameId(Player p) {
		for (Game g: games) {
			if (g.isPlayerActive(p)) {
				return g.getID();
			}
		}
		return -1;
	}

	public int getPlayerSpectateId(Player p) {
		for (Game g: games) {
			if (g.isSpectator(p)) {
				return g.getID();
			}
		}
		return -1;
	}

	public boolean isPlayerActive(Player player) {
		for (Game g: games) {
			if (g.isPlayerActive(player)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPlayerInactive(Player player) {
		for (Game g: games) {
			if (g.isPlayerInactive(player)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSpectator(Player player) {
		for (Game g: games) {
			if (g.isSpectator(player)) {
				return true;
			}
		}
		return false;
	}

	public void removeFromOtherQueues(Player p, int id) {
		for (Game g: getGames()) {
			if (g.isInQueue(p) && g.getID() != id) {
				g.removeFromQueue(p);
				msgmgr.sendMessage(PrefixType.INFO, "Removed from the queue in arena " + g.getID(), p);
			}
		}
	}

	public boolean isInKitMenu(Player p){
		return kitsel.contains(p);
	}

	public void leaveKitMenu(Player p){
		kitsel.remove(p);
	}

	public void openKitMenu(Player p){
		kitsel.add(p);
	}

	public void selectKit(Player p, int i) {
		p.getInventory().clear();
		ArrayList<Kit>kits = getKits(p);
		if(i <= kits.size()){
			Kit k = getKits(p).get(i);
			if(k!=null){
				p.getInventory().setContents(k.getContents().toArray(new ItemStack[0]));
			}
		}
		p.updateInventory();

	}

	public int getGameCount() {
		return games.size();
	}

	public Game getGame(int a) {
		//int t = gamemap.get(a);
		for (Game g: games) {
			if (g.getID() == a) {
				return g;
			}
		}
		return null;
	}

	public void removePlayer(Player p, boolean b) {
		getGame(getPlayerGameId(p)).removePlayer(p, b);
	}

	public void removeSpectator(Player p) {
		getGame(getPlayerSpectateId(p)).removeSpectator(p);
	}

	public void disableGame(int id) {
		getGame(id).disable();
	}

	public void enableGame(int id) {
		getGame(id).enable();
	}

	public ArrayList < Game > getGames() {
		return games;
	}

	public GameMode getGameMode(int a) {
		for (Game g: games) {
			if (g.getID() == a) {
				return g.getMode();
			}
		}
		return null;
	}

	public ArrayList<Kit> getKits(Player p){
		ArrayList<Kit>k = new ArrayList<Kit>();
		for(Kit kit: kits){
			if(kit.canUse(p)){
				k.add(kit);
			}
		}
		return k;
	}

	//TODO: Actually make this countdown correctly
	public void startGame(int a) {
		getGame(a).countdown(10);
	}

	public void addPlayer(Player p, int g) {
		Game game = getGame(g);
		if (game == null) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.input",p, "message-No game by this ID exist! id=" + g);
			return;
		}
		getGame(g).addPlayer(p);
	}
	public void addPlayer(Player p, Game game) {
		if (game == null) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.input",p, "message-Game doesn't exist!");
			return;
		}
		game.addPlayer(p);
	}
	public void addPlayerRandomly(Player p) {
		//gets list of joinable games
		ArrayList <Game> joinableGames = new ArrayList <Game> ();
		for(Game game : games) {
			if(game.isJoinable()) {
				joinableGames.add(game);
			}
		}
		if(joinableGames.isEmpty()) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.noJoinableGames",p, "&cNo joinable arenas found!!");
			return;
		}
		
		Random rnd = ThreadLocalRandom.current();
		int r = rnd.nextInt(joinableGames.size());
		Game game = joinableGames.get(r);

		addPlayer(p, game);
	}

	public void autoAddPlayer(Player pl) {
		//gets list of joinable games
		ArrayList <Game> joinableGames = new ArrayList <Game> ();
		for(Game game : games) {
			if(game.isJoinable()) {
				joinableGames.add(game);
			}
		}
		if(joinableGames.isEmpty()) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.noJoinableGames",pl, "&cNo joinable arenas found!!");
			return;
		}
			
		//if there are games starting we just pick one of these randomly
		Game game = null;
		ArrayList <Game> bestGames = new ArrayList <Game> ();
		for(Game g : joinableGames) {
			if(g.getMode() == GameMode.STARTING) {
				bestGames.add(g);
			}
		}
		if(!bestGames.isEmpty()) {
			Random rnd = ThreadLocalRandom.current();
			int r = rnd.nextInt(bestGames.size());
			game = bestGames.get(r);
		}else {//else we get the game with the lowest estimate time
			Game bestGame = null;
			for(Game g : joinableGames) {
				float esTime = g.getEstimateTimeToStart();
				if((bestGame == null || esTime < bestGame.getEstimateTimeToStart()) && esTime != -1)  {
					bestGame = g;
				}
			}
			if(bestGame == null) {//if all arenas return a invalid estimate time we just join a random joinable arena
				Random rnd = ThreadLocalRandom.current();
				int r = rnd.nextInt(joinableGames.size());
				bestGame = joinableGames.get(r);
			}
			game = bestGame;
		}
		
		
	
		addPlayer(pl, game);
	}

	public WorldEditPlugin getWorldEdit() {
		return p.getWorldEdit();
	}

	@SuppressWarnings("unused")
	public void createArenaFromSelection(Player pl) {
		FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
		//SettingsManager s = SettingsManager.getInstance();
		
		
		Location max = null;
		Location min = null;
		try {
			if(SurvivalGames.PRE1_13) {
				Object sel2;
				WorldEditPlugin we = p.getWorldEdit();
				sel2 = ReflectionUtils.getSelection.invoke(we, pl);
				if (sel2 == null) {
					msgmgr.sendMessage(PrefixType.WARNING, "You must make a WorldEdit Selection first!", pl);
					return;
				}				
				max = (Location) ReflectionUtils.getMaximumPoint.invoke(sel2);
				min = (Location) ReflectionUtils.getMinimumPoint.invoke(sel2);
				
			}else {
				Region sel;
				BukkitPlayer bpl = BukkitPlayer.class.cast(ReflectionUtils.adapt.invoke(ReflectionUtils.BukkitAdapterClass, pl));
				LocalSession localsesion = WorldEdit.getInstance().getSessionManager().get(bpl);
				WorldEditPlugin we = p.getWorldEdit();			
				try {
					sel = localsesion.getSelection(bpl.getWorld());				
				}catch(IncompleteRegionException e){
					msgmgr.sendMessage(PrefixType.WARNING, "You must make a WorldEdit Selection first!", pl);
					return;
				}
				Object maxV = sel.getMaximumPoint();
				max = new Location(pl.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(maxV).toString()));
				Object minV = sel.getMinimumPoint();
				min = new Location(pl.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(minV).toString()));
	
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
		if(max == null || min == null) {
			msgmgr.sendMessage(PrefixType.WARNING, "You must make a WorldEdit Selection first!", pl);
			return;
		}
		/* if(max.getWorld()!=SettingsManager.getGameWorld() || min.getWorld()!=SettingsManager.getGameWorld()){
            pl.sendMessage(ChatColor.RED+"Wrong World!");
            return;
        }*/

		int no = c.getInt("sg-system.arenano") + 1;
		c.set("sg-system.arenano", no);
		if (games.size() == 0) {
			no = 1;
		} else no = games.get(games.size() - 1).getID() + 1;
		SettingsManager.getInstance().getSpawns().set(("spawns." + no), null);
		c.set("sg-system.arenas." + no + ".world", max.getWorld().getName());
		c.set("sg-system.arenas." + no + ".x1", max.getBlockX());
		c.set("sg-system.arenas." + no + ".y1", max.getBlockY());
		c.set("sg-system.arenas." + no + ".z1", max.getBlockZ());
		c.set("sg-system.arenas." + no + ".x2", min.getBlockX());
		c.set("sg-system.arenas." + no + ".y2", min.getBlockY());
		c.set("sg-system.arenas." + no + ".z2", min.getBlockZ());
		c.set("sg-system.arenas." + no + ".enabled", true);

		SettingsManager.getInstance().saveSystemConfig();
		hotAddArena(no);
		pl.sendMessage(ChatColor.GREEN + "Arena ID " + no + " Succesfully added");

	}

	private void hotAddArena(int no) {
		Game game = new Game(no);
		games.add(game);
		StatsManager.getInstance().addArena(no);
		//SurvivalGames.$("game added "+ games.size()+" "+SettingsManager.getInstance().getSystemConfig().getInt("gs-system.arenano"));
	}

	public void hotRemoveArena(int no) {
		for (Game g: games.toArray(new Game[0])) {
			if (g.getID() == no) {
				games.remove(getGame(no));
			}
		}
	}

	public void gameEndCallBack(int id) {
		// Chest reset is now done by a separate class
		//getGame(id).setRBStatus("clearing chest");
		//openedChest.put(id, new HashMap < Block, ItemStack[] > ());
	}

	public String getStringList(int gid){
		Game g = getGame(gid);
		StringBuilder sb = new StringBuilder();
		Player[][]players = g.getPlayers();

		sb.append(ChatColor.GREEN+"<---------------------[ Alive: "+players[0].length+" ]--------------------->\n"+ChatColor.GREEN+" ");
		for(Player p: players[0]){
			sb.append(p.getName()+",");
		}
		sb.append("\n\n");
		sb.append(ChatColor.RED+  "<---------------------[ Dead: "+players[1].length+" ]---------------------->\n"+ChatColor.GREEN+" ");
		for(Player p: players[1]){
			sb.append(p.getName()+",");
		}
		sb.append("\n\n");

		return sb.toString();
	}
}