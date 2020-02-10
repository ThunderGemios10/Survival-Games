package com.thundergemios10.survivalgames.events;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.SurvivalGames;



public class BreakEvent implements Listener {

    public ArrayList<Material> allowedBreak =  new ArrayList<Material>();
    static Method getMaterial = getMaterialMethod();

    public BreakEvent(){
    	for(String material : SettingsManager.getInstance().getConfig().getStringList("block.break.whitelist")) {
    		try {
	    		if(SurvivalGames.PRE1_13) {
	    			allowedBreak.add((Material) getMaterial.invoke(Material.class, material));
	    		}else {
					allowedBreak.add((Material) getMaterial.invoke(Material.class, material, SurvivalGames.LEGACY_ITEM_LOAD));
	    		}
    		} catch (Exception  e) {
				e.printStackTrace();
			}
    	}
    	SurvivalGames.debug("BreakEvent: read : " + allowedBreak.toString());
    }

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        int pid = GameManager.getInstance().getPlayerGameId(p);


        if(pid == -1){
            int blockgameid  = GameManager.getInstance().getBlockGameId(event.getBlock().getLocation());

            if(blockgameid != -1){
                if(GameManager.getInstance().getGame(blockgameid).getGameMode() != Game.GameMode.DISABLED){
                    event.setCancelled(true);
                }
            }
            return;
        }


        Game g = GameManager.getInstance().getGame(pid);

        if(g.getMode() == Game.GameMode.DISABLED){
            return;
        }
        if(g.getMode() != Game.GameMode.INGAME){
            event.setCancelled(true);
            return;
        }

        if(allowedBreak.contains(event.getBlock().getType())) {
        	event.setCancelled(false);
		}else {
			event.setCancelled(true);
		}
    }
	private static Method getMaterialMethod() {
		try {
			if(SurvivalGames.PRE1_13) {
				return Material.class.getMethod("matchMaterial", String.class);
			}else {
				return Material.class.getMethod("matchMaterial", String.class, boolean.class);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}