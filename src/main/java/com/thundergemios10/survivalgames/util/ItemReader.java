package com.thundergemios10.survivalgames.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.thundergemios10.survivalgames.SurvivalGames;

public class ItemReader {

	
	private static HashMap<String, Enchantment>encids;
	static Method getMaterial = getMaterialMethod();

	
	private static void loadIds(){
		
		encids =  new HashMap<String, Enchantment>();
		
		
		for(Enchantment e:Enchantment.values()){
			encids.put(e.toString().toLowerCase().replace("_", ""), e);
		}
		
		//Anything enchants
		encids.put("unbreaking", Enchantment.DURABILITY);
		encids.put("mending", Enchantment.MENDING);
		
		//Armor Enchants
		encids.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
		encids.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
		encids.put("fireprot", Enchantment.PROTECTION_FIRE);
		encids.put("fireprotection", Enchantment.PROTECTION_FIRE);
		encids.put("featherfall", Enchantment.PROTECTION_FALL);
		encids.put("featherfalling", Enchantment.PROTECTION_FALL);
		encids.put("blastprot", Enchantment.PROTECTION_EXPLOSIONS);
		encids.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
		encids.put("projectileprot", Enchantment.PROTECTION_PROJECTILE);
		encids.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
		encids.put("aquaaffinity", Enchantment.WATER_WORKER);
		encids.put("respiration", Enchantment.OXYGEN);
		encids.put("thorns", Enchantment.THORNS);
		encids.put("depthstrider", Enchantment.DEPTH_STRIDER);
		encids.put("frostwalker", Enchantment.FROST_WALKER);
		
		//Weapon Enchants
		encids.put("knockback", Enchantment.KNOCKBACK);
		encids.put("smite", Enchantment.DAMAGE_UNDEAD);
		encids.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
		encids.put("sharpness", Enchantment.DAMAGE_ALL);
		encids.put("dmg", Enchantment.DAMAGE_ALL);
		encids.put("fire", Enchantment.FIRE_ASPECT);
		encids.put("looting", Enchantment.LOOT_BONUS_MOBS);
		encids.put("loot", Enchantment.LOOT_BONUS_MOBS);
		encids.put("sweepingedge", Enchantment.SWEEPING_EDGE);
		
		//Tool enchants (Silk Touch's enchantment name is Silk_Touch, so it's covered above)
		encids.put("silktouch", Enchantment.SILK_TOUCH);
		encids.put("efficiency", Enchantment.DIG_SPEED);
		encids.put("fort", Enchantment.LOOT_BONUS_BLOCKS);
		encids.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
		
		//Bow specific enchants
		encids.put("punch", Enchantment.ARROW_KNOCKBACK);
		encids.put("power", Enchantment.ARROW_DAMAGE);
		encids.put("infinity", Enchantment.ARROW_INFINITE);
		encids.put("flame", Enchantment.ARROW_FIRE);

		//Fishing Rod specific enchants
		encids.put("luckofthesea", Enchantment.LUCK);
		encids.put("lure", Enchantment.LURE);
	}
	
	

	public static ItemStack read(String str){
		if(encids == null){
			loadIds();
		}
		String split[] = str.split(",");
		SurvivalGames.debug("ItemReader: reading : "+Arrays.toString(split));
		for(int a = 0; a < split.length; a++){
			split[a] = split[a].trim();
		}
		String materialString = split[0].toUpperCase();
		try {
		if(split.length < 1){
			return null;
		}else if(split.length == 1){
    		if(SurvivalGames.PRE1_13) {
    			return new ItemStack((Material) getMaterial.invoke(Material.class, materialString));
    		}else {
    			return new ItemStack((Material) getMaterial.invoke(Material.class, materialString, SurvivalGames.LEGACY_ITEM_LOAD));
    		}
		}else if(split.length == 2){
    		if(SurvivalGames.PRE1_13) {
    			return new ItemStack((Material) getMaterial.invoke(Material.class, materialString), Integer.parseInt(split[1]));
    		}else {
    			return new ItemStack((Material) getMaterial.invoke(Material.class, materialString, SurvivalGames.LEGACY_ITEM_LOAD), Integer.parseInt(split[1]));
    		}
		}else if(split.length == 3){
    		if(SurvivalGames.PRE1_13) {
    			ItemStack item = new ItemStack((Material) getMaterial.invoke(Material.class, materialString), Integer.parseInt(split[1]));
    			item.setDurability(Short.parseShort(split[2]));
    			return item;
    		}else {
    			ItemStack item = new ItemStack((Material) getMaterial.invoke(Material.class, materialString, SurvivalGames.LEGACY_ITEM_LOAD), Integer.parseInt(split[1]));
    			item.setDurability(Short.parseShort(split[2]));
    			return item;
    		}
		}else{
			ItemStack i = null;
    		if(SurvivalGames.PRE1_13) {
    			ItemStack item = new ItemStack((Material) getMaterial.invoke(Material.class, materialString), Integer.parseInt(split[1]));
    			item.setDurability(Short.parseShort(split[2]));
    			i = item;
    		}else {
    			ItemStack item = new ItemStack((Material) getMaterial.invoke(Material.class, materialString, SurvivalGames.LEGACY_ITEM_LOAD), Integer.parseInt(split[1]));
    			item.setDurability(Short.parseShort(split[2]));
    			i = item;
    		}

			if (!split[3].equalsIgnoreCase("none")) {
				String encs[] = split[3].toLowerCase().split(" ");
				for(String enc: encs){
					System.out.println(enc);
					String e[] = enc.split(":");
					i.addUnsafeEnchantment(encids.get(e[0]), Integer.parseInt(e[1]));
				}
			}
			if(split.length == 5){
				ItemMeta im = i.getItemMeta();
				im.setDisplayName(MessageUtil.replaceColors(split[4]));
				i.setItemMeta(im);
			}
			return i;
		}	
		}catch(Exception e) {
			e.printStackTrace();
			return new ItemStack(Material.DIRT);
		}
	}
		
	public static String getFriendlyItemName(Material m){
		String str = m.toString();
		str = str.replace('_',' ');
		str = str.substring(0, 1).toUpperCase() +
				str.substring(1).toLowerCase();
		return str;
	}
	
	private static Method getMaterialMethod() {
		try {
			if(SurvivalGames.PRE1_13) {
				return Material.class.getMethod("getMaterial", String.class);
			}else {
				return Material.class.getMethod("getMaterial", String.class, boolean.class);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
