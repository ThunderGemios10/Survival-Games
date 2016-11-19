package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class ListPlayers implements SubCommand{

	public boolean onCommand(Player player, String[] args) {
		int gid = 0;
		try{
			if(args.length == 0){
				gid = GameManager.getInstance().getPlayerGameId(player);
			}
			else{
				gid =  Integer.parseInt(args[0]);
			}

			String[] msg = GameManager.getInstance().getStringList(gid).split("\n");
			player.sendMessage(msg);
			return false;
        } catch (NumberFormatException ex) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
        } catch (NullPointerException ex) {
            MessageManager.getInstance().sendMessage(MessageManager.PrefixType.ERROR, "error.gamenoexist", player);
        }
		return false;
	}

	public String help(Player p) {
        return "/sg list [<id>] " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.listplayers","List all players in the arena you are playing in");
	}

	public String permission() {
		return "";
	}

}