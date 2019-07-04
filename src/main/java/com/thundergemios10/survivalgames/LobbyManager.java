package com.thundergemios10.survivalgames;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.thundergemios10.survivalgames.util.ReflectionUtils;

public class LobbyManager {

	private static LobbyManager instance = new LobbyManager();
	private HashMap < Integer, ArrayList < LobbyWall >> signs = new HashMap < Integer, ArrayList < LobbyWall >> ();
	public static HashSet < Chunk > lobbychunks = new HashSet < Chunk > ();
	FileConfiguration s = SettingsManager.getInstance().getSystemConfig();

	private LobbyManager() {

	}

	public static LobbyManager getInstance() {
		return instance;
	}

	public void setup(SurvivalGames p) {
		loadSigns();
	}

	public void loadSigns() {
	
		for (int a = 1; a <= s.getInt("sg-system.lobby.signno"); a++) {
			loadSign(a);
		}
		//Bukkit.getScheduler().scheduleSyncRepeatingTask(GameManager.getInstance().getPlugin(), new updater(), 100L, 20L);
	}

	public void loadSign(int a) {
		try{
		SurvivalGames.debug("sg-system.lobby.signs." + a + ".world");
		World w = Bukkit.getWorld(s.getString("sg-system.lobby.signs." + a + ".world"));
		int x1 = s.getInt("sg-system.lobby.signs." + a + ".x1");
		int y1 = s.getInt("sg-system.lobby.signs." + a + ".y1");
		int z1 = s.getInt("sg-system.lobby.signs." + a + ".z1");
		int x2 = s.getInt("sg-system.lobby.signs." + a + ".x2");
		//int y2 = s.getInt("sg-system.lobby.signs." + a + ".y2");
		int z2 = s.getInt("sg-system.lobby.signs." + a + ".z2");
		int gameid = s.getInt("sg-system.lobby.signs." + a + ".id");

		LobbyWall ls = new LobbyWall(gameid);
		if (ls.loadSign(w, x1, x2, z1, z2, y1)) {
			ArrayList < LobbyWall > t = signs.get(gameid);
			if (t == null) {
				t = new ArrayList < LobbyWall > ();
				signs.put(gameid, t);
			}
			t.add(ls);
			ls.update(); //TODO
		}
		else{
			/*s.set("sg-system.lobby.signs." + a, null); 
			SettingsManager.getInstance().saveSystemConfig();*/
		}
		}catch(Exception e){
			s.set("sg-system.lobby.signs." + a, null);
			s.set("sg-system.lobby.signno", s.getInt("sg-system.lobby.signno")-1);
		}
	}

	/*	class updater implements Runnable{
		public void run(){
			updateall();
		}
	}*/

	public void updateAll() {
		for (ArrayList < LobbyWall > lws: signs.values()) {
			for (LobbyWall lw: lws) {
				lw.update();
			}
		}
	}

	public void updateWall(int a) {
		if(signs.get(a) != null){
			for (LobbyWall lw: signs.get(a)) {
				lw.update();
			}
		}
	}
	
	public void removeSignsForArena(int a){
		clearAllSigns();
		signs.remove(a);
		updateAll();
	}

	public void clearSigns(int a) {
		if(signs.get(a) != null){
			for (LobbyWall ls: signs.get(a)) {
				ls.clear();
			}
		}
	}

	public void clearAllSigns() {
		for (ArrayList < LobbyWall > lws: signs.values()) {
			for (LobbyWall lw: lws) {
				lw.clear();
			}
		}
	}

	public void display(String s, int a) {
		if(signs.get(a) != null){

			for (LobbyWall ls: signs.get(a)) {
				ls.addMsg(s);
			}
		}
	}

	public void display(String[] s, int a) {
		for (String s1: s) {
			display(s1, a);
		}
	}

	public void setLobbySignsFromSelection(Player pl, int a) {
		FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
		SettingsManager s = SettingsManager.getInstance();
		if (!c.getBoolean("sg-system.lobby.sign.set", false)) {
			c.set("sg-system.lobby.sign.set", true);
			s.saveSystemConfig();
		}
		WorldEditPlugin we = GameManager.getInstance().getWorldEdit();
		
		Location max = null;
		Location min = null;
		try {
			if(SurvivalGames.PRE1_13) {
				Object sel2;
				sel2 = ReflectionUtils.getSelection.invoke(we, pl);
				if (sel2 == null) {
					pl.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
					return;
				}				
				max = (Location) ReflectionUtils.getMaximumPoint.invoke(sel2);
				min = (Location) ReflectionUtils.getMinimumPoint.invoke(sel2);
	
			}else {
				Region sel;
				BukkitPlayer bpl = BukkitPlayer.class.cast(ReflectionUtils.adapt.invoke(ReflectionUtils.BukkitAdapterClass, pl));
				LocalSession localsesion = WorldEdit.getInstance().getSessionManager().get(bpl);		
				try {
					sel = localsesion.getSelection(bpl.getWorld());				
				}catch(IncompleteRegionException e){
					pl.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
					return;
				}
				Object maxV = sel.getMaximumPoint();
				max = new Location(pl.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(maxV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(maxV).toString()));
				Object minV = sel.getMinimumPoint();
				min = new Location(pl.getWorld(), Double.parseDouble(ReflectionUtils.getBlockX.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockY.invoke(minV).toString()),  Double.parseDouble(ReflectionUtils.getBlockZ.invoke(minV).toString()));	
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return;
		}
		
		if (max  == null || min == null) {
			pl.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
			return;
		}
		if ((max.getBlockX() - min.getBlockX()) != 0 && (min.getBlockZ() - max.getBlockZ() != 0)) {
			pl.sendMessage(ChatColor.RED + " Must be in a straight line!");
			return;
		}
		int i = c.getInt("sg-system.lobby.signno", 0) + 1;
		c.set("sg-system.lobby.signno", i);
		c.set("sg-system.lobby.signs." + i + ".id", a);
		c.set("sg-system.lobby.signs." + i + ".world", pl.getWorld().getName());
		c.set("sg-system.lobby.signs." + i + ".x1", max.getBlockX());
		c.set("sg-system.lobby.signs." + i + ".y1", max.getBlockY());
		c.set("sg-system.lobby.signs." + i + ".z1", max.getBlockZ());
		c.set("sg-system.lobby.signs." + i + ".x2", min.getBlockX());
		c.set("sg-system.lobby.signs." + i + ".y2", min.getBlockY());
		c.set("sg-system.lobby.signs." + i + ".z2", min.getBlockZ());
		pl.sendMessage(ChatColor.GREEN + "Added Lobby Wall"); //TODO
		s.saveSystemConfig();
		loadSign(i);
	}
}