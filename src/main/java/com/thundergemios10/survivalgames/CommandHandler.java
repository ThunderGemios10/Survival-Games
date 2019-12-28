package com.thundergemios10.survivalgames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import com.thundergemios10.survivalgames.MessageManager.PrefixType;
import com.thundergemios10.survivalgames.commands.*;


public class CommandHandler implements CommandExecutor {
	private static CommandHandler instance;
	private Plugin plugin;
	private HashMap < String, SubCommand > commands;
	private HashMap < String, Integer > helpinfo;
	private HashMap<String, ConsoleSubCommand> nonPlayerOnlyCmds = new HashMap<String, ConsoleSubCommand>(); 
	private MessageManager msgmgr = MessageManager.getInstance();
	public List<String> tabCompletionList = new ArrayList<String>();
	public CommandHandler(Plugin plugin) {
		setInstance(this);
		this.plugin = plugin;
		commands = new HashMap < String, SubCommand > ();
		helpinfo = new HashMap < String, Integer > ();
		loadCommands();
		loadHelpInfo();
		loadNonPlayerOnlyCmdList();
		loadTabCompletionList();
	}

	private void loadCommands() {
//		commands.put("stet", new StartTimeEstimationTest());	//Start Time Estimation Test - for testing the getEstimateTimeToStart function in game class
		commands.put("createarena", new CreateArena());
		commands.put("join", new Join());
		commands.put("autojoin", new AutoJoin());
		commands.put("addwall", new AddWall());
		commands.put("setspawn", new SetSpawn());
		commands.put("getcount", null);
		commands.put("disable", null);
		commands.put("forcestart", null);
		commands.put("enable", null);
		commands.put("vote", new Vote());
		commands.put("leave", new Leave());
		commands.put("setlobbyspawn", new SetLobbySpawn());
		commands.put("setlobbywall", new SetLobbyWall());
		commands.put("resetspawns", new ResetSpawns());
		commands.put("delarena", null);
		commands.put("flag", null);
		commands.put("spectate", new Spectate());
		commands.put("lq", new LeaveQueue());
		commands.put("leavequeue", new LeaveQueue());
		commands.put("list", null);
		commands.put("listarenas", null);
		commands.put("tp", new Teleport());
		commands.put("reload", null);
		commands.put("refill", new Refill());
//		commands.put("setstatswall", new SetStatsWall());
		commands.put("resetarena", null);
//		commands.put("test", new Test());

		// commands.put("sponsor", new Sponsor());
	}

	private void loadHelpInfo() {
		//you can do this by iterating thru the hashmap from a certain index btw instead of using a new hashmap,
		//plus, instead of doing three different ifs, just iterate thru and check if the value == the page
		helpinfo.put("join", 1);
		helpinfo.put("autojoin", 1);
		helpinfo.put("vote", 1);
		helpinfo.put("spectate", 1);
		helpinfo.put("lq", 1);
		helpinfo.put("leavequeue", 1);
		helpinfo.put("list", 1);
		helpinfo.put("listarenas", 1);
		helpinfo.put("leave", 1);
		helpinfo.put("disable", 2);
		helpinfo.put("start", 2);
		helpinfo.put("enable", 2);
		helpinfo.put("createarena", 3);
		helpinfo.put("addwall", 3);
		helpinfo.put("setspawn", 3);
		helpinfo.put("getcount", 3);
		helpinfo.put("setlobbyspawn", 3);
		helpinfo.put("resetspawns", 3);
		helpinfo.put("delarena", 3);
		helpinfo.put("flag", 3);
		helpinfo.put("reload", 3);
		helpinfo.put("refill", 3);
		helpinfo.put("resetarena", 3);
		//helpinfo.put("setstatswall", 1);

		//helpinfo.put("sponsor", 1);
	}
	private void loadNonPlayerOnlyCmdList() {
		nonPlayerOnlyCmds.put("forcestart", new ForceStart());
		nonPlayerOnlyCmds.put("enable", new Enable());
		nonPlayerOnlyCmds.put("disable", new Disable());
		nonPlayerOnlyCmds.put("delarena", new DelArena());
		nonPlayerOnlyCmds.put("reload",  new Reload());
		nonPlayerOnlyCmds.put("resetarena",  new ResetArena());
		nonPlayerOnlyCmds.put("listarenas", new ListArenas());
		nonPlayerOnlyCmds.put("getcount", new ListArenas());//Same as listarenas 
		nonPlayerOnlyCmds.put("list", new ListPlayers());
		nonPlayerOnlyCmds.put("flag", new Flag());
	}
	private void loadTabCompletionList(){
		for (String key : commands.keySet()) {
			tabCompletionList.add(key);
		}
		
	}

	public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();

		if (SurvivalGames.config_todate == false) {
			msgmgr.sendMessage(PrefixType.WARNING, "The config file is out of date. Please tell an administrator to reset the config.", sender);
			return true;
		}

		if (SurvivalGames.dbcon == false) {
			msgmgr.sendMessage(PrefixType.WARNING, "Could not connect to server. Plugin disabled.", sender);
			return true;
		}

		if (cmd1.getName().equalsIgnoreCase("survivalgames")) {
			if (args == null || args.length < 1) {
				msgmgr.sendMessage(PrefixType.INFO, "Version " + pdfFile.getVersion() + " originally by Double0negative", sender);
				msgmgr.sendMessage(PrefixType.INFO, "Later fixes and updates by ThunderGemios10, SShipway and remyboy2003", sender);
				msgmgr.sendMessage(PrefixType.INFO, "Type /sg help <player | staff | admin> for command information", sender);
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1) {
					help(sender, 1);
				}
				else {
					if (args[1].toLowerCase().startsWith("player")) {
						help(sender, 1);
						return true;
					}
					if (args[1].toLowerCase().startsWith("staff")) {
						help(sender, 2);
						return true;
					}
					if (args[1].toLowerCase().startsWith("admin")) {
						help(sender, 3);
						return true;
					}
					else {
						msgmgr.sendMessage(PrefixType.WARNING, args[1] + " is not a valid page! Valid pages are Player, Staff, and Admin.", sender);
					}
				}
				return true;
			}			
			
			String sub = args[0];
			Vector < String > l = new Vector < String > ();
			l.addAll(Arrays.asList(args));
			l.remove(0);
			args = (String[]) l.toArray(new String[0]);
			if (!commands.containsKey(sub)) {
				msgmgr.sendMessage(PrefixType.WARNING, "Command doesn't exist.", sender);
				msgmgr.sendMessage(PrefixType.INFO, "Type /sg help for command information", sender);
				return true;
			}
			try {
				if(nonPlayerOnlyCmds.containsKey(sub)) {
					nonPlayerOnlyCmds.get(sub).onCommand(sender, args);
				}else if(sender instanceof Player){
					Player player = (Player) sender;
					commands.get(sub).onCommand(player, args);
				}else {
					msgmgr.logMessage(PrefixType.WARNING, "Only in-game players can use this SurvivalGames command! ");
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				msgmgr.sendFMessage(PrefixType.ERROR, "error.command", sender, "command-["+sub+"] "+Arrays.toString(args));
				msgmgr.sendMessage(PrefixType.INFO, "Type /sg help for command information", sender);
			}
			return true;
		}
		return false;
	}

	public void help (CommandSender s, int page) {
		if (page == 1) {
			s.sendMessage(ChatColor.BLUE + "------------ " + msgmgr.pre + ChatColor.DARK_AQUA + " Player Commands" + ChatColor.BLUE + " ------------");
		}
		if (page == 2) {
			s.sendMessage(ChatColor.BLUE + "------------ " + msgmgr.pre + ChatColor.DARK_AQUA + " Staff Commands" + ChatColor.BLUE + " ------------");
		}
		if (page == 3) {
			s.sendMessage(ChatColor.BLUE + "------------ " + msgmgr.pre + ChatColor.DARK_AQUA + " Admin Commands" + ChatColor.BLUE + " ------------");
		}

		for (String command : commands.keySet()) {
			try{
				if (helpinfo.get(command) == page) {
					if(nonPlayerOnlyCmds.containsKey(command)) {
						msgmgr.sendMessage(PrefixType.INFO, nonPlayerOnlyCmds.get(command).help(), s);
					}else {
						msgmgr.sendMessage(PrefixType.INFO, commands.get(command).help(), s);	
					}
				}
			} catch(Exception e) {}
		}
		/*for (SubCommand v : commands.values()) {
            if (v.permission() != null) {
                if (p.hasPermission(v.permission())) {
                    msgmgr.sendMessage(PrefixType.INFO1, v.help(p), p);
                } else {
                    msgmgr.sendMessage(PrefixType.WARNING, v.help(p), p);
                }
            } else {
                msgmgr.sendMessage(PrefixType.INFO, v.help(p), p);
            }
        }*/
	}
	private void setInstance(CommandHandler instance) {
		CommandHandler.instance = instance;
	}
	public static CommandHandler getInstance() {
		return instance;
	}
}
