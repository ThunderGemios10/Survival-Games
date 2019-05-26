package com.thundergemios10.survivalgames.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.SettingsManager;



public class ForceStart implements ConsoleSubCommand {

	MessageManager msgmgr = MessageManager.getInstance();
	private boolean isPlayer = false;

	public boolean onCommand(CommandSender sender, String[] args) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
			isPlayer = true;
		}

		if (!sender.hasPermission(permission()) && !sender.isOp() ) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", sender);
			return true;
		}
		int game = -1;
		int seconds = 10;
		if(args.length == 2){
			seconds = Integer.parseInt(args[1]);
		}
		if(args.length >= 1){
			game = Integer.parseInt(args[0]);

		}
		else if(isPlayer)
			game  = GameManager.getInstance().getPlayerGameId(player);
		if(game == -1){
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notingame", sender);
			return true;
		}
		
		Game g = GameManager.getInstance().getGame(game);
		if(g == null) {
			MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.gamedoesntexist", sender, "arena-" + args[0]);
			return true;
		}
		
		if(g.getActivePlayers() < 2){
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notenoughplayers", sender);
			return true;
		}

		if (g.getMode() != Game.GameMode.WAITING && !sender.hasPermission("sg.arena.restart")) {
			MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.alreadyingame", sender);
			return true;
		}
		g.countdown(seconds);

		msgmgr.sendFMessage(PrefixType.INFO, "game.started", sender, "arena-" + game);

		return true;
	}

	public String help() {
		return "/sg forcestart - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.forcestart", "Forces the game to start");
	}

	public String permission() {
		return "sg.arena.start";
	}
}