package com.thundergemios10.survivalgames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.thundergemios10.survivalgames.CommandHandler;

public class TabCompletion implements TabCompleter {
	private List<String> emptylist = new ArrayList<String>();
	private List<String> TabCompletionList = CommandHandler.getInstance().tabCompletionList;
	
	public TabCompletion() {
		TabCompletionList.add("help");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("survivalgames")) {
			if(args.length == 1) {
				List<String> list = new ArrayList<String>();	
				
				for (String string : TabCompletionList) {
					if (string.startsWith(args[0].toLowerCase())) {
						list.add(string);
					}
				}
				return list;
				
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("help")) {
					List<String> helpList = new ArrayList<String>();
					List<String> helpListOut = new ArrayList<String>();
					helpList.add("player");
					helpList.add("staff");
					helpList.add("admin");
					
					for(String string : helpList) {
						if (string.startsWith(args[1].toLowerCase())) {
							helpListOut.add(string);
						}
					}
					return helpListOut;
				}else if(args[0].equalsIgnoreCase("reload")) {
					List<String> helpList = new ArrayList<String>();
					List<String> helpListOut = new ArrayList<String>();
					helpList.add("all");
					helpList.add("games");
					helpList.add("settings");
					
					for(String string : helpList) {
						if (string.startsWith(args[1].toLowerCase())) {
							helpListOut.add(string);
						}
					}
					return helpListOut;
				}
			}
		}
		return emptylist;
	}
}

