package com.thundergemios10.survivalgames.commands;

import org.bukkit.command.CommandSender;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.Game.GameMode;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class Enable implements ConsoleSubCommand{

	public boolean onCommand(CommandSender sender, String[] args) {      
		if(!sender.hasPermission(permission()) && !sender.isOp()){
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", sender);
			return true;
		}
		try{
			if(args.length == 0){
				for(Game g:GameManager.getInstance().getGames()){
					if(g.getMode() == GameMode.DISABLED)
						g.enable();
				}
				MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.INFO, "game.all", sender, "input-enabled");
			}
			else{
				GameManager.getInstance().enableGame(Integer.parseInt(args[0]));
				MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.INFO, "game.state", sender, "arena-" + args[0], "input-enabled");
			}
		} catch (NumberFormatException e) {
			MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", sender, "input-Arena");
		} catch (NullPointerException e) {
			MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.gamedoesntexist", sender, "arena-" + args[0]);
		}
		return true;

	}


	public String help() {
		return "/sg enable <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.enable", "Enables arena <id>");
	}

	public String permission() {
		return "sg.arena.enable";
	}
}