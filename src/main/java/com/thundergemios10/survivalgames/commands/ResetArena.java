package com.thundergemios10.survivalgames.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.logging.QueueManager;
import com.thundergemios10.survivalgames.SettingsManager;

public class ResetArena implements ConsoleSubCommand {

	MessageManager msgmgr = MessageManager.getInstance();
	
	private boolean isPlayer = false;

	public boolean onCommand(CommandSender sender, String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
			isPlayer = true;
		}
		
		if (!sender.hasPermission(permission()) && !sender.isOp()) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", sender);
			return true;
		}
		int game = -1;
		if(args.length >= 1){
			game = Integer.parseInt(args[0]);

		}
		else if(isPlayer)
			game  = GameManager.getInstance().getPlayerGameId(player);
		if(game == -1){
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notingame", sender);
			return true;
		}

		//Game g = GameManager.getInstance().getGame(game);
		
		QueueManager.getInstance().rollback(game, true); // forced fast rollback - blocks, chests, entities
		
		msgmgr.sendFMessage(PrefixType.INFO, "game.reset", sender, "arena-" + game);

		return true;
	}

	public String help() {
		return "/sg resetarena [<id>] " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.resetarena", "Reset the arena to start state");
	}

	public String permission() {
		return "sg.arena.reset";
	}
}