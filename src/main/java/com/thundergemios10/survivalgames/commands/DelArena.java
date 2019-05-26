package com.thundergemios10.survivalgames.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.LobbyManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;



public class DelArena implements ConsoleSubCommand{

    public boolean onCommand(CommandSender sender, String[] args) {	
        if (!sender.hasPermission(permission()) && !sender.isOp()){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", sender);
            return true;
        }
        
        if(args.length != 1){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notspecified", sender, "input-Arena");
            return true;
        }
        
        FileConfiguration s = SettingsManager.getInstance().getSystemConfig();
        //FileConfiguration spawn = SettingsManager.getInstance().getSpawns();
        int arena = Integer.parseInt(args[0]);
        Game g = GameManager.getInstance().getGame(arena);
        
        if(g == null){
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.gamedoesntexist", sender, "arena-" + arena);
            return true;
        }
        
        g.disable();
        s.set("sg-system.arenas."+arena+".enabled", false);
        s.set("sg-system.arenano", s.getInt("sg-system.arenano") - 1);
        //spawn.set("spawns."+arena, null);
        MessageManager.getInstance().sendFMessage(PrefixType.INFO, "info.deleted", sender, "input-Arena");
        SettingsManager.getInstance().saveSystemConfig();
        GameManager.getInstance().hotRemoveArena(arena);
        //LobbyManager.getInstance().clearAllSigns();
        LobbyManager.getInstance().removeSignsForArena(arena);
        return true;
    }

    public String help() {
        return "/sg delarena <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.delarena", "Delete an arena");
    }

	public String permission() {
		return "sg.admin.deletearena";
	}   
}