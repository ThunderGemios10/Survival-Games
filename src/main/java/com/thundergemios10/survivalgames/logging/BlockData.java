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
    private byte prevdata,newdata;
    private int x,y,z;
    private int gameid;
    private ItemStack[] items;
    
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

    public byte getPrevdata() {
        return prevdata;
    }

    public byte getNewdata() {
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
