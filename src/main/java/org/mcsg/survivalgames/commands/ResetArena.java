package org.mcsg.survivalgames.commands;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcsg.survivalgames.Game;
import org.mcsg.survivalgames.GameManager;
import org.mcsg.survivalgames.MessageManager;
import org.mcsg.survivalgames.MessageManager.PrefixType;
import org.mcsg.survivalgames.logging.QueueManager;
import org.mcsg.survivalgames.SettingsManager;

public class ResetArena implements SubCommand {

	MessageManager msgmgr = MessageManager.getInstance();

	public boolean onCommand(Player player, String[] args) {

		if (!player.hasPermission(permission()) && !player.isOp()) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", player);
			return true;
		}
		int game = -1;
		if(args.length >= 1){
			game = Integer.parseInt(args[0]);

		}
		else
			game  = GameManager.getInstance().getPlayerGameId(player);
		if(game == -1){
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notingame", player);
			return true;
		}

		//Game g = GameManager.getInstance().getGame(game);
		
		QueueManager.getInstance().rollback(game, true); // forced fast rollback - blocks, chests, entities
		
		msgmgr.sendFMessage(PrefixType.INFO, "game.reset", player, "arena-" + game);

		return true;
	}

	public String help(Player p) {
		return "/sg resetarena [<id>] " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.resetarena", "Reset the arena to start state");
	}

	public String permission() {
		return "sg.arena.reset";
	}
}