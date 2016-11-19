package com.thundergemios10.survivalgames.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class CommandCatch implements Listener{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String m = event.getMessage();

        if(!GameManager.getInstance().isPlayerActive(event.getPlayer()) && !GameManager.getInstance().isPlayerInactive(event.getPlayer()) && !GameManager.getInstance().isSpectator(event.getPlayer()))
            return;
        if(m.equalsIgnoreCase("/list")){
            event.getPlayer().sendMessage(
            		GameManager.getInstance().getStringList(
            				GameManager.getInstance().getPlayerGameId(event.getPlayer())));
            return;
        }
        if(!SettingsManager.getInstance().getConfig().getBoolean("disallow-commands"))
            return;
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("sg.staff.nocmdblock"))
            return;
        else if(m.startsWith("/sg") || m.startsWith("/survivalgames")|| m.startsWith("/hg")||m.startsWith("/hungergames")||m.startsWith("/msg")){
            return;
        }
        else if (SettingsManager.getInstance().getConfig().getStringList("cmdwhitelist").contains(m)) {
        	return;
        }
        event.setCancelled(true);
    }
}
