package com.thundergemios10.survivalgames.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.thundergemios10.survivalgames.SettingsManager;

public class Kit {
	// This thing hasn't been fully implemented. I should probably start working on this.

	private String name;
	private double cost;
	private ArrayList<ItemStack>items = new ArrayList<ItemStack>();
	private ItemStack icon;
	
	
	
	
	public Kit(String name){
		this.name = name;
		load();
	}
	
	
	public void load(){
		FileConfiguration c = SettingsManager.getInstance().getKits();
		cost = c.getDouble("kits."+name+".cost", 0);
		
		icon = ItemReader.read(c.getString("kits."+name+".icon"));
		System.out.println(icon);
		List<String>cont = c.getStringList("kits."+name+".contents");
		for(String s:cont){
			items.add(ItemReader.read(s));
		}
		
	}
	
	public ArrayList<ItemStack> getContents(){
		return items;
	}
	
	
	public boolean canUse(Player p){
		return p.hasPermission("sg.kit."+name);
	}


	public String getName() {
		return name;
	}
	
	public ItemStack getIcon(){
		return icon;
	}
	
	public double getCost(){
		return cost;
	}
}
