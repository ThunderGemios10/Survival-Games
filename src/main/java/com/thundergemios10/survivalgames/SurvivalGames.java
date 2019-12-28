package com.thundergemios10.survivalgames;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.craftbukkit.v1_12_R1.generator.InternalChunkGenerator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.thundergemios10.survivalgames.events.*;
import com.thundergemios10.survivalgames.hooks.HookManager;
import com.thundergemios10.survivalgames.logging.LoggingManager;
import com.thundergemios10.survivalgames.logging.QueueManager;
import com.thundergemios10.survivalgames.stats.StatsManager;
import com.thundergemios10.survivalgames.util.ChestRatioStorage;
import com.thundergemios10.survivalgames.util.DatabaseManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

//import org.bstats.bukkit.Metrics;

public class SurvivalGames extends JavaPlugin {
	public static Logger logger;
	private static File datafolder;
	private static boolean disabling = false;
	public static boolean dbcon = false;
	public static boolean config_todate = false;
	public static int config_version = 3;
	public static boolean PRE1_13;
	public static final boolean LEGACY_ITEM_LOAD = false;

	public static List < String > auth = Arrays.asList(new String[] {
			"Double0negative", "iMalo", "Medic0987", "alex_markey", "skitscape", "AntVenom", "YoshiGenius", "pimpinpsp", "WinryR", "Jazed2011",
			"KiwiPantz", "blackracoon", "CuppingCakes", "4rr0ws", "Fawdz", "Timothy13", "rich91", "ModernPrestige", "Snowpool", "egoshk", 
			"nickm140",  "chaseoes", "Oceangrass", "GrailMore", "iAngelic", "Lexonia", "ChaskyT", "Anon232", "IngeniousGamer", "ThunderGemios10", "sshipway", "HeroCC", "remyboy2003" // List of Contributors
	});

	SurvivalGames p = this;
	private static SurvivalGames instance;
	public void onDisable() {
		disabling = false;
		PluginDescriptionFile pdfFile = p.getDescription();
		SettingsManager.getInstance().saveSpawns();
		SettingsManager.getInstance().saveSystemConfig();
		for (Game g: GameManager.getInstance().getGames()) {
			try {
				g.disable();
			} catch(Exception e) {
				// Will throw useless "tried to register task blah blah error." Use the method below to reset the arena without a task.
			}
			QueueManager.getInstance().rollback(g.getID(), true);
		}

		logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " has now been disabled and reset");
	}

	public void onEnable() {
		logger = p.getLogger();
		setInstance(this);
		
		//check if server is pre 1.13
		if (Integer.parseInt(getServer().getVersion().replaceAll("[^\\d.]", "").split("\\.")[1]) < 13) {
			PRE1_13 = true;
		}else {
			PRE1_13 = false;
		}
		if(PRE1_13) {
			getServer().getConsoleSender().sendMessage("[SurvivalGames] Running pre 1.13");
		}else {
			getServer().getConsoleSender().sendMessage("[SurvivalGames] Running 1.13+");
		}
		
		
		// Ensure that all worlds are loaded. Fixes some issues with Multiverse loading after this plugin had started
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Startup(), 10);

		
		@SuppressWarnings("unused")
		Metrics	metrics = new Metrics(this);

	}

	class Startup implements Runnable {
		public void run() {
			datafolder = p.getDataFolder();

			PluginManager pm = getServer().getPluginManager();
			setCommands();
			

			SettingsManager.getInstance().setup(p);
			MessageManager.getInstance().setup();
			GameManager.getInstance().setup(p);

			try { // Try loading everything that uses SQL. 
				FileConfiguration c = SettingsManager.getInstance().getConfig();
				if (c.getBoolean("stats.enabled")) DatabaseManager.getInstance().setup(p);
				QueueManager.getInstance().setup();
				StatsManager.getInstance().setup(p, c.getBoolean("stats.enabled"));
				dbcon = true;
			} catch (Exception e) {
				dbcon = false;
				e.printStackTrace();
				logger.severe("!!!Failed to connect to the database. Please check the settings and try again!!!");
				return;
			} finally {
				LobbyManager.getInstance().setup(p);
			}

			ChestRatioStorage.getInstance().setup();
			HookManager.getInstance().setup();
			pm.registerEvents(new PlaceEvent(), p);
			pm.registerEvents(new BreakEvent(), p);
			pm.registerEvents(new DeathEvent(), p);
			pm.registerEvents(new MoveEvent(), p);
			pm.registerEvents(new CommandCatch(), p);
			pm.registerEvents(new SignClickEvent(), p);
			pm.registerEvents(new ChestReplaceEvent(), p);
			pm.registerEvents(new LogoutEvent(), p);
			pm.registerEvents(new JoinEvent(p), p);
			pm.registerEvents(new TeleportEvent(), p);
			pm.registerEvents(LoggingManager.getInstance(), p);
			pm.registerEvents(new SpectatorEvents(), p);
			pm.registerEvents(new BandageUse(), p);
			pm.registerEvents(new KitEvents(), p);
			pm.registerEvents(new KeepLobbyLoadedEvent(), p);


			for (Player p: Bukkit.getOnlinePlayers()) {
				if (GameManager.getInstance().getBlockGameId(p.getLocation()) != -1) {
					p.teleport(SettingsManager.getInstance().getLobbySpawn());
				}
			}

			//   new Webserver().start();
		}
	}

	public void setCommands() {
		getCommand("survivalgames").setExecutor(new CommandHandler(p));
		this.getCommand("survivalgames").setTabCompleter(new com.thundergemios10.survivalgames.commands.TabCompletion());
	}




	public static File getPluginDataFolder() {
		return datafolder;
	}

	public static boolean isDisabling() {
		return disabling;
	}

	public WorldEditPlugin getWorldEdit() {
		Plugin worldEdit = getServer().getPluginManager().getPlugin("WorldEdit");
		if (worldEdit instanceof WorldEditPlugin) {
			return (WorldEditPlugin) worldEdit;
		} else {
			return null;
		}
	}

	public static void $(String msg){
		logger.log(Level.INFO, msg);
	}

	public static void $(Level l, String msg){
		logger.log(l, msg);
	}

	public static void debug(String msg){
		if(SettingsManager.getInstance().getConfig().getBoolean("debug", false))
			$("[Debug] "+msg);
	}

	public static void info(String msg){
		$(Level.INFO,msg);
	}
	
	public static void warning(String msg){
		$(Level.WARNING,"[Warning] "+msg);
	}
	
	public static void error(String msg){
		$(Level.SEVERE,"[Error] "+msg);
	}
	
	public static void debug(int a) {
		if(SettingsManager.getInstance().getConfig().getBoolean("debug", false))
			debug(a+"");
	}
	private void setInstance(SurvivalGames instance) {
		SurvivalGames.instance = instance;
	}
	public static SurvivalGames getInstance() {
		return instance;
	}
}
