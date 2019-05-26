package com.thundergemios10.survivalgames.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.GameManager;
import com.thundergemios10.survivalgames.SurvivalGames;
import com.thundergemios10.survivalgames.util.ReflectionUtils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class Test implements SubCommand{

	public boolean onCommand(Player player, String[] args) {
		WorldEditPlugin we = GameManager.getInstance().getWorldEdit();
		Location max = null;
		Location min = null;
		try {
			if(SurvivalGames.PRE1_13) {
				Object sel2;
				sel2 = ReflectionUtils.getSelection.invoke(we, player);
				if (sel2 == null) {
					player.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
					return false;
				}
				max = (Location) ReflectionUtils.getMaximumPoint.invoke(sel2);
				min = (Location) ReflectionUtils.getMinimumPoint.invoke(sel2);
				
			}else {
				Region sel;
				BukkitPlayer bpl = BukkitPlayer.class.cast(ReflectionUtils.adapt.invoke(ReflectionUtils.BukkitAdapterClass, player));
				LocalSession localsesion = WorldEdit.getInstance().getSessionManager().get(bpl);		
				try {
					sel = localsesion.getSelection(bpl.getWorld());				
				}catch(IncompleteRegionException e){
					player.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
					return false;
				}
				Object maxV = sel.getMaximumPoint();
				max = new Location(player.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(maxV).toString()));
				Object minV = sel.getMinimumPoint();
				min = new Location(player.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(minV).toString()));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
		
		if (max  == null || min == null) {
			return false;
		}
		
		World w = max.getWorld();
		
		HashSet<Location> mark = new HashSet<Location>();
		
		for(int a = min.getBlockZ(); a < max.getBlockZ(); a++){
			mark.add(getYLocation(w,max.getBlockX(), max.getBlockY(), a));
			mark.add(getYLocation(w,min.getBlockX(), max.getBlockY(), a));
		}
		for(int a = min.getBlockX(); a < max.getBlockX(); a++){
			mark.add(getYLocation(w,a, max.getBlockY(), max.getBlockZ()));
			mark.add(getYLocation(w,a, max.getBlockY(), min.getBlockZ()));
		}
		
		setFence(mark);
		return true;
		
	}
	
	public Location getYLocation(World w, int x, int y, int z){
		Location l = new Location(w,x,y,z);
		while(l.getBlock().getType().equals(Material.AIR)){
			l.add(0,-1,0);
		}
		return l.add(0,1,0);
	}
	
	public void setFence(HashSet<Location> locs){
		for(Location l: locs){
			Material material = null;
			try {
				if(SurvivalGames.PRE1_13) {
					material = Material.class.cast(Material.class.getDeclaredField("FENCE"));
				}else {				
					material = Material.class.cast(Material.class.getDeclaredField("LEGACY_FENCE"));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			l.getBlock().setType(material);
		}
	}

	public String help() {
		// TODO Auto-generated method stub
		return null;
	}

	public String permission() {
		// TODO Auto-generated method stub
		return null;
	}

}
