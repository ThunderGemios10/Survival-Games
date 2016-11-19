package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class CreateArena implements SubCommand{

    public boolean onCommand(Player player, String[] args) {
        if(!player.hasPermission(permission()) && !player.isOp()){
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.nopermission", player);
            return true;
        }
        GameManager.getInstance().createArenaFromSelection(player);
        return true;
    }

    public String help(Player p) {
        return "/sg createarena - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.createarena", "Create a new arena with the current WorldEdit selection");
    }

	public String permission() {
		return "sg.admin.createarena";
	}
}