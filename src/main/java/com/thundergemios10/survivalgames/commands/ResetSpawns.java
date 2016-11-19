package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;



public class ResetSpawns implements SubCommand{

    public boolean onCommand(Player player, String[] args) {
        
        if (!player.hasPermission(permission()) && !player.isOp()) {
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", player);
            return true;
        }
        try{
        SettingsManager.getInstance().getSpawns().set("spawns."+Integer.parseInt(args[0]), null);
        return true;
                } catch (NumberFormatException e) {
                    MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
                } catch (NullPointerException e) {
                    MessageManager.getInstance().sendMessage(MessageManager.PrefixType.ERROR, "error.gamenoexist", player);
                }
        return true;
    }   

    public String help(Player p) {
        return "/sg resetspawns <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.resetspawns", "Resets spawns for Arena <id>");
    }

	public String permission() {
		return "sg.admin.resetspawns";
	}
}