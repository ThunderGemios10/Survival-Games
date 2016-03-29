package org.mcsg.survivalgames.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mcsg.survivalgames.SurvivalGames;

public class BandageUse implements Listener {

	@EventHandler
	public void onBandageUse(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().getItemInHand().getType() == Material.PAPER) {
				e.getPlayer().getInventory().removeItem(new ItemStack(Material.PAPER, 1));
				double newhealth = e.getPlayer().getHealth() + 10;
				if((newhealth > 20.0) || (newhealth < 0 )) { newhealth = 20.0; }
				e.getPlayer().setHealth(newhealth);
				e.getPlayer().sendMessage(ChatColor.GREEN + "You used a bandage and got 5 hearts.");
			}
		}
	}
}