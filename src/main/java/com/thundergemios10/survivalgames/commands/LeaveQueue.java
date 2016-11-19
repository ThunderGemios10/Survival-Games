package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class LeaveQueue implements SubCommand{

    public boolean onCommand(Player player, String[] args) {
        GameManager.getInstance().removeFromOtherQueues(player, -1);
        return true;
    }

    public String help(Player p) {
        return "/sg lq - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.leavequeue", "Leave the queue for any queued games");
    }

	public String permission() {
		return null;
	}

}
