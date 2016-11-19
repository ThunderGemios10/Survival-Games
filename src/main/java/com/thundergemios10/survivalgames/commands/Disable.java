package com.thundergemios10.survivalgames.commands;

import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.Game;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.MessageManager;
import com.thundergemios10.survivalgames.SettingsManager;



public class Disable implements SubCommand{

    public boolean onCommand(Player player, String[] args) {        
        if(!player.hasPermission(permission()) && !player.isOp()){
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.nopermission", player);
            return true;
        }
        try{
        if(args.length == 0){
            for(Game g: GameManager.getInstance().getGames()){
                g.disable();
            }
                MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.INFO, "game.all", player, "input-disabled");

        }else{

            GameManager.getInstance().disableGame(Integer.parseInt(args[0]));
                MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.INFO, "game.state", player, "arena-" + args[0], "input-disabled");
        }
        } catch (NumberFormatException e) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notanumber", player, "input-Arena");
        } catch (NullPointerException e) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.gamedoesntexist", player, "arena-" + args[0]);
        }
        return true;
    }
    
    public String help(Player p) {
        return "/sg disable <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.disable", "Disables arena <id>");
    }

	public String permission() {
		return "sg.arena.disable";
	}
}
