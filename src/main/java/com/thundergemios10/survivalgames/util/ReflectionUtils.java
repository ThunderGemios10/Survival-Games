package com.thundergemios10.survivalgames.util;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.thundergemios10.survivalgames.SurvivalGames;

public class ReflectionUtils {
	
	//1.13+ worldEdit
	public static Class<?> BukkitAdapterClass =  getBukkitAdapterClass();
	public static Class<?> BlockVector3Class = getBlockVector3Class();
	public static Method adapt = adaptMethod();
	public static Method getBlockX = getBlockXMethod();
	public static Method getBlockY = getBlockYMethod();
	public static Method getBlockZ = getBlockZMethod();
	
	//1.12 worldEdit
	public static Class<?> selectionClass = getSelectionClass();
	public static Method getMaximumPoint = getMaximumPointMethod();
	public static Method getMinimumPoint = getMinimumPointMethod();
	public static Method getSelection = getSelectionMethod();
	
	//1.13 worldEdit
	private static Class<?> getBukkitAdapterClass() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Class<?> getBlockVector3Class() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return Class.forName("com.sk89q.worldedit.math.BlockVector3");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Method adaptMethod() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return BukkitAdapterClass.getMethod("adapt", Player.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Method getBlockXMethod() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return BlockVector3Class.getMethod("getBlockX");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Method getBlockYMethod() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return BlockVector3Class.getMethod("getBlockY");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Method getBlockZMethod() {
		if(SurvivalGames.PRE1_13) return null;
		try {
			return BlockVector3Class.getMethod("getBlockZ");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	//1.12 worldEdit
	private static Class<?> getSelectionClass() {
		if(!SurvivalGames.PRE1_13) return null;
		try {
			return Class.forName("com.sk89q.worldedit.bukkit.selections.Selection");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		return null;
	}
	private static Method getMaximumPointMethod() {
		if(!SurvivalGames.PRE1_13) return null;
		try {
			return selectionClass.getMethod("getMaximumPoint");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return getBlockX;	
	}
	private static Method getMinimumPointMethod() {
		if(!SurvivalGames.PRE1_13) return null;
		try {
			return selectionClass.getMethod("getMinimumPoint");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return getBlockX;	
	}
	private static Method getSelectionMethod() {
		if(!SurvivalGames.PRE1_13) return null;
		try {
			return WorldEditPlugin.class.getMethod("getSelection", Player.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return getBlockX;	
	}
}
