package com.thundergemios10.survivalgames.commands;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.SurvivalGames;


public class StartTimeEstimationTest implements SubCommand {
	private ArrayList<UUID> uuids = new ArrayList<UUID> ();
	private int iterator = 0;
	private Date startTime;

	public boolean onCommand(final Player player, String[] args) {
		if(player.hasPermission(permission())){
			if(args.length < 1) {
                MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notspecified", player, "input-Game ID");
                return true;
			}
			int a;
			try {
				a = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notanumber", player, "input-" + args[0]);
				return true;
			}
			
			final Game game = GameManager.getInstance().getGame(a);
			if(game == null ) return true;
			game.disable();
			game.enable();
			
			final int maxPlayers = SettingsManager.getInstance().getSpawnCount(a);
			
			uuids.ensureCapacity(maxPlayers);
			
			for(int i = 0; i < maxPlayers; i++) {
				uuids.add(UUID.randomUUID());
			}
			
			startTime = new Date();
			
			
	;
			
			if(args[2].equalsIgnoreCase("true")) {
			//synthetic test
			for(int i = 0; i < maxPlayers; i++) {

				game.addPlayerJoinTime(uuids.get(i), startTime.getTime() + (args.length > 1 ? Long.parseLong(args[1]) : 1000) * i);
				
				float esTime = game.getEstimateTimeToStart();
				
				boolean invalid = esTime == -1 ? true : false;
				
				Date esDate = addSecondsToTime(new Date(), esTime);
				
				int count = i + 1;
				
				MessageManager.getInstance().sendMessage(PrefixType.INFO, "Added player " + count + "/" + maxPlayers + " to game. Estimate time: " + (invalid ?  "invalid" : getTimeString(esDate)  + "  raw estimate: " + esTime), player);
			}
			game.disable();
			game.enable();
			}else {
	//			less synthetic test
				MessageManager.getInstance().sendMessage(PrefixType.INFO, "Starting time estimation test starttime: " + getTimeString(startTime), player);
				new BukkitRunnable() {
								
					@Override
					public void run() {
						if(!(iterator < maxPlayers)) {
							MessageManager.getInstance().sendMessage(PrefixType.INFO, "Finished at: " + getTimeString(new Date()), player);
							game.disable();
							game.enable();
							iterator = 0;
							this.cancel();
							return;
						}
						
						game.addPlayerJoinTime(uuids.get(iterator), new Date().getTime());
						float esTime = game.getEstimateTimeToStart();
						
						boolean invalid = esTime == -1 ? true : false;
						
						Date esDate = addSecondsToTime(new Date(), esTime);
						
						int count = iterator + 1;
						
						MessageManager.getInstance().sendMessage(PrefixType.INFO, "Added player " + count + "/" + maxPlayers + " to game. Estimate time: " + (invalid ?  "invalid" : getTimeString(esDate)  + "  raw estimate: " + esTime), player);
						
						iterator++;
					}
				}.runTaskTimer(SurvivalGames.getInstance(), 0, (long) ((args.length > 1 ? Float.parseFloat(args[1]) : 1) * 20));
			}

			

		}
		else{
			MessageManager.getInstance().sendFMessage(PrefixType.WARNING, "error.nopermission", player);
		}
		return true;
	}

	public String help() {
		return "/sg StartTimeEstimationTest <arenaid> [interval]- " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.randomjoin", "Joins a random game");//TODO add a help message
	}

	public String permission() {
		return "sg.admin.TimeEstimationTest";
	}
	
	private String getTimeString(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}
	private Date addSecondsToTime(Date time, float esTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.SECOND, (int) esTime);
		return cal.getTime();
	}
}

