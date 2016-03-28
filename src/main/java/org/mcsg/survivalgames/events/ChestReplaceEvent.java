package org.mcsg.survivalgames.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.SettingsManager;
import org.mcsg.survivalgames.Game.GameMode;
import org.mcsg.survivalgames.SurvivalGames;
import org.mcsg.survivalgames.util.ChestRatioStorage;



public class ChestReplaceEvent implements Listener{

	private Random rand = new Random();
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ChestListener(PlayerInteractEvent e){
    	if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
    		Block blk = e.getClickedBlock();
    		BlockState clicked = blk.getState();
    		if(clicked instanceof Chest || clicked instanceof DoubleChest){
    			int gameid = GameManager.getInstance().getPlayerGameId(e.getPlayer());
    			if(gameid != -1){
    				Game game = GameManager.getInstance().getGame(gameid);
    				if(game.getMode() == GameMode.INGAME){
    					HashMap<Block,ItemStack[]>openedChest = GameManager.openedChest.get(gameid);
    					openedChest = (openedChest == null)? new HashMap<Block,ItemStack[]>() : openedChest;
    					if(!openedChest.containsKey(e.getClickedBlock())){
    						Inventory[] invs = ((clicked instanceof Chest))? new Inventory[] {((Chest) clicked).getBlockInventory()}
    						: new Inventory[] {((DoubleChest)clicked).getLeftSide().getInventory(), ((DoubleChest)clicked).getRightSide().getInventory()};
    						SurvivalGames.debug("ChestReplaceEvent: "+blk.getX()+","+blk.getY()+","+blk.getZ());
    						// copy the original contents
    						ItemStack[] oldinv = invs[0].getContents().clone();
    						int level = 1; // default level is 1
    						// if first item in chest is wool, use colour code + 1 for the level
    						ItemStack item = invs[0].getItem(0);
    						if ( (item != null && item.getType() == Material.WOOL) ) {
    							level = item.getData().getData() + 1;
    							SurvivalGames.debug("Setting level to "+level);
    							invs[0].removeItem(item);
    						}
    						level = ChestRatioStorage.getInstance().getLevel(level); // randomly go up by up to 4 levels
    						// if this is a double chest, it will have 2 inventories, else only one
    						for(Inventory inv : invs){
        						if( SettingsManager.getInstance().getConfig().getBoolean("clear",false)) {
        							SurvivalGames.debug("Clearing initial content");
        							inv.setContents(new ItemStack[inv.getContents().length]);
        						}
    				            for(ItemStack i: ChestRatioStorage.getInstance().getItems(level)){
    				                int l = rand.nextInt(26);
    				                while(inv.getItem(l) != null)
    				                    l = rand.nextInt(26);
    				                inv.setItem(l, i);
    				            }
    						}
        					openedChest.put(e.getClickedBlock(),oldinv);
        					GameManager.openedChest.put(gameid, openedChest);
    					}
    				} else {
    					e.setCancelled(true);
    					return;
    				}
    			}
    		}
    	}
    }
    	
    	
    	
    	
    	
    	
    	
    	/*
    	 * 
    	 * OLD CRAP CODE
    	
        try{
        	
            HashSet<Block>openedChest3 = new HashSet<Block>();

            if(!(e.getAction()==Action.RIGHT_CLICK_BLOCK)) return;

            Block clickedBlock = e.getClickedBlock(); 
            int gameid = GameManager.getInstance().getPlayerGameId(e.getPlayer());
            if(gameid == -1) return;
            GameManager gm = GameManager.getInstance();
            
            if(!gm.isPlayerActive(e.getPlayer())){
                return;
            }
        
            if(gm.getGame(gameid).getMode() != GameMode.INGAME){
            	e.setCancelled(true);
                return;
            }
            
            if(GameManager.openedChest.get(gameid) !=null){
                openedChest3.addAll(GameManager.openedChest.get(gameid));
            }
            
            if(openedChest3.contains(clickedBlock)){
                return;
            }
            
            Inventory inv;
            int size = 0;
            
            if (clickedBlock.getState() instanceof Chest) {
                size = 1;
                inv  = ((Chest) clickedBlock.getState()).getInventory();

            }
            else if(clickedBlock.getState() instanceof DoubleChest){
                size = 2;
                inv = ((DoubleChest) clickedBlock.getState()).getInventory();

            }
            else return;

            inv.clear();
            Random r = new Random();

            for(ItemStack i: ChestRatioStorageOLD.getInstance().getItems()){
                int l = r.nextInt(26 * size);

                while(inv.getItem(l) != null)
                    l = r.nextInt(26 * size);
                inv.setItem(l, i);


            }
            openedChest3.add(clickedBlock);
            GameManager.openedChest.put(gameid, openedChest3);
        }
        catch(Exception e5){}*/



}
