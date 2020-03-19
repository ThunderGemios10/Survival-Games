package com.thundergemios10.survivalgames.events;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.SurvivalGames;



public class PlaceEvent implements Listener {

    public  ArrayList<Material> allowedPlace = new ArrayList<Material>();
    static Method getMaterial = getMaterialMethod();

    public PlaceEvent(){
    	for(String material : SettingsManager.getInstance().getConfig().getStringList("block.place.whitelist")) {
    		try {
	    		if(SurvivalGames.PRE1_13) {
	    			allowedPlace.add((Material) getMaterial.invoke(Material.class, material));
	    		}else {
	    			allowedPlace.add((Material) getMaterial.invoke(Material.class, material, SurvivalGames.LEGACY_ITEM_LOAD));
	    		}
    		} catch (Exception  e) {
				e.printStackTrace();
			}
    	}
    	SurvivalGames.debug("PlaceEvent: read : " + allowedPlace.toString());
    }

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        int id  = GameManager.getInstance().getPlayerGameId(p);

        if(id == -1){
            int gameblockid = GameManager.getInstance().getBlockGameId(event.getBlock().getLocation());
            if(gameblockid != -1){
                if(GameManager.getInstance().getGame(gameblockid).getGameMode() != Game.GameMode.DISABLED){
                    event.setCancelled(true);
                }
            }
            return;
        }


        Game g = GameManager.getInstance().getGame(id);
        if(g.isPlayerInactive(p)){
            return;
        }
        if(g.getMode() == Game.GameMode.DISABLED){
            return;
        }
        if(g.getMode() != Game.GameMode.INGAME){
            event.setCancelled(true);
            return;

        }

        if(allowedPlace.contains(event.getBlock().getType())) {
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
