package com.thundergemios10.survivalgames.logging;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String world;
    private Material prevType;
    private Material newType;
    private byte prevdataPRE1_13,newdataPRE1_13;
    private int x,y,z;
    private int gameid;
    private ItemStack[] items;
	private Object prevdata,newdata;
    
    /**
     * 
     * @param previd
     * @param newid
     * @param x
     * @param y
     * @param z
     * 
     * Provides a object for holding the data for block changes
     */
    public BlockData(int gameid, String world, Material prevType,byte prevdata, Material newType,byte newdata, int x, int y, int z, ItemStack[] items){
        this.gameid = gameid;
        this.world = world;
        this.prevType = prevType;
        this.prevdataPRE1_13 = prevdata;
        this.newType = newType;
        this.newdataPRE1_13 = newdata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.items = items;
    }
    public BlockData(int gameid, String world, Material prevType,Object prevdata, Material newType,Object newdata, int x, int y, int z, ItemStack[] items){
        this.gameid = gameid;
        this.world = world;
        this.prevType = prevType;
        this.prevdata = prevdata;
        this.newType = newType;
        this.newdata = newdata;
        this.x = x;
        this.y = y;
        this.z = z;
        this.items = items;
    }
    
    public int getGameId(){
        return gameid;
    }
    
    public String getWorld(){
        return world;
    }

    public byte getPrevdataPRE1_13() {
        return prevdataPRE1_13;
    }
    public Object getPrevdata() {
        return prevdata;
    }

    public byte getNewdataPRE1_13() {
        return newdataPRE1_13;
    }
    public Object getnewdata() {
        return newdata;
    }
    
    public Material getPrevType() {
        return prevType;
    }

    public Material getNewType() {
        return newType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public ItemStack[] getItems(){
    	return items;
    }
}
