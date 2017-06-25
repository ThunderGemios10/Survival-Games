package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
// import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.stats.StatsWallManager;



public class SetStatsWall implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        StatsWallManager.getInstance().setStatsSignsFromSelection(player);
        return false;
    }

    
    public String help(Player p){
        return null; //"/sg setstatswall - "+ SettingsManager.getInstance().getMessageConfig().getString("messages.help.setstatswall", "Sets the stats wall");
    }

	public String permission() {
		return "sg.admin.setstatswall";
	}
}
