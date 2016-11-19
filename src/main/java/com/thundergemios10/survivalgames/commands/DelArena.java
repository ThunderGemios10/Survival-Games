package com.thundergemios10.survivalgames.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.LobbyManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;



public class DelArena implements SubCommand{

    public boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission(permission()) && !player.isOp()){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", player);
            return true;
        }
        
        if(args.length != 1){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notspecified", player, "input-Arena");
            return true;
        }
        
        FileConfiguration s = SettingsManager.getInstance().getSystemConfig();
        //FileConfiguration spawn = SettingsManager.getInstance().getSpawns();
        int arena = Integer.parseInt(args[0]);
        Game g = GameManager.getInstance().getGame(arena);
        
        if(g == null){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.gamedoesntexist", player, "arena-" + arena);
            return true;
        }
        
        g.disable();
        s.set("sg-system.arenas."+arena+".enabled", false);
        s.set("sg-system.arenano", s.getInt("sg-system.arenano") - 1);
        //spawn.set("spawns."+arena, null);
        MessageManager.getInstance().sendFMessage(PrefixType.INFO, "info.deleted", player, "input-Arena");
        SettingsManager.getInstance().saveSystemConfig();
        GameManager.getInstance().hotRemoveArena(arena);
        //LobbyManager.getInstance().clearAllSigns();
        LobbyManager.getInstance().removeSignsForArena(arena);
        return true;
    }

    public String help (Player p) {
        return "/sg delarena <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.delarena", "Delete an arena");
    }

	public String permission() {
		return "sg.admin.deletearena";
	}   
}