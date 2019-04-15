package com.thundergemios10.survivalgames.stats;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.thundergemios10.survivalgames.SettingsManager;
import com.thundergemios10.survivalgames.SurvivalGames;
import com.thundergemios10.survivalgames.util.ReflectionUtils;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class StatsWallManager {

    

    

    //TODO: Possibly clean up
    Sign[][] signs;
    SurvivalGames p;
    private int runningThread = 0;
    private static StatsWallManager instance = new StatsWallManager();
    private StatsWallManager(){

    }

    public static StatsWallManager getInstance(){
        return instance;

    }


    public void setup(SurvivalGames p){
        this.p = p;
        loadSigns();
    }


    public void loadSigns(){

        FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
        try{
            if(!c.getBoolean("sg-system.stats.sign.set"))
                return;
        }catch(Exception e){return;}
        boolean usingx = false;
        int hdiff = 0;
        int x1 = c.getInt("sg-system.stats.sign.x1");
        int y1 = c.getInt("sg-system.stats.sign.y1");
        int z1 = c.getInt("sg-system.stats.sign.z1");

        int x2 = c.getInt("sg-system.stats.sign.x2");
        int y2 = c.getInt("sg-system.stats.sign.y2");
        int z2 = c.getInt("sg-system.stats.sign.z2");
        int inc = 0;
        Location l;
        //  System.out.println(x1+"  "+y1+"  "+z1);
        @SuppressWarnings("deprecation")
		byte temp = ((Sign)new Location(p.getServer().getWorld(c.getString("sg-system.stats.sign.world")),x1,y1,z1).getBlock().getState()).getData().getData();
        //  System.out.println("facing "+temp);
        if(temp == 3 || temp == 4){
            l = new Location(Bukkit.getWorld(c.getString("sg-system.stats.sign.world")),x1,y1,z1);
            inc = -1;
        }else{
            l = new Location(Bukkit.getWorld(c.getString("sg-system.stats.sign.world")),x2,y1,z2);
            inc = 1;
        }


        usingx = ((x2 - x1) != 0)? true : false;
        if(usingx){
            hdiff = (x1 - x2)+1;
        }
        else{
            hdiff = (z1 - z2) +1;
        }
        int vdiff = (y1 - y2 ) + 1;


        System.out.println(vdiff+"              "+hdiff);
        signs = new Sign[vdiff][hdiff];
        for(int y = vdiff-1; y>=0; y--){
            for(int x = hdiff-1; x>=0;x--){


                BlockState b =   p.getServer().getWorld(SettingsManager.getInstance().getSystemConfig().getString("sg-system.stats.sign.world")).getBlockAt(l).getState();
                if(b instanceof Sign){
                    signs[y][x] = (Sign)b;
                }
                if(usingx)
                    l = l.add(inc, 0, 0);
                else
                    l = l.add(0, 0, inc);
                //l.getBlock().setTypeId(323);
            }
            l = l.add(0, -1, 0);
            if(inc == -1){
                l.setX(x1);
                l.setZ(z1);
            }
            else{
                l.setX(x2);
                l.setZ(z2);
            }
        }
        runningThread ++;
    }

    public int[] getSignMidPoint(){
        double x = (signs[0].length * 8);
        double y = (signs.length * 2);

        return new int[]{(int)x,(int)y};
    }



    public void setStatsSignsFromSelection(Player pl){
        FileConfiguration c = SettingsManager.getInstance().getSystemConfig();
        SettingsManager s = SettingsManager.getInstance();
        if(!c.getBoolean("sg-system.stats.sign.set", false)){
            c.set("sg-system.stats.sign.set", true);
            s.saveSystemConfig();
        }

        WorldEditPlugin we = p.getWorldEdit();
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
		}
		
		if (max  == null || min == null) {
			pl.sendMessage(ChatColor.RED + "You must make a WorldEdit Selection first");
			return;
		}
        if( (max.getBlockX()  - max.getBlockX()) != 0 && (max.getBlockZ()  - max.getBlockZ() !=0)){
            pl.sendMessage(ChatColor.RED +" Must be in a straight line!");
            return;
        }

        c.set("sg-system.stats.sign.world", pl.getWorld().getName());
        c.set("sg-system.stats.sign.x1", max.getBlockX());
        c.set("sg-system.stats.sign.y1", max.getBlockY());
        c.set("sg-system.stats.sign.z1", max.getBlockZ());
        c.set("sg-system.stats.sign.x2", min.getBlockX());
        c.set("sg-system.stats.sign.y2", min.getBlockY());
        c.set("sg-system.stats.sign.z2", min.getBlockZ());

        pl.sendMessage(ChatColor.GREEN+"Stats wall successfully created");
        s.saveSystemConfig();
        loadSigns();


    }
    
    class StatsSignUpdater extends Thread{
        @SuppressWarnings("unused")
		public void run(){
            int trun = runningThread;

           /* while(SurvivalGames.isActive() && trun == runningThread){
                try{
                    try{Thread.sleep(1000);}catch(Exception e){}
                    updateStatsWall();
                }catch(Exception e){e.printStackTrace(); signs[0][0].setLine(1, ChatColor.RED+"ERROR");signs[0][0].setLine(1, ChatColor.RED+"Check Console");}
            }*/
        }
    }

    //String sorts = {""
    
    public void updateStatsWall() {
        
        
    }
    
    
}
