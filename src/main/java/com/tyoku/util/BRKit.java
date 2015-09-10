package com.tyoku.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class BRKit {
    public static List<ItemStack> getAlchemistKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ItemStack item1 = new ItemStack(Material.POTION, 1);
        Potion pot = new Potion(PotionType.INSTANT_DAMAGE);
        pot.setSplash(true);
        pot.apply(item1);
        ret.add(item1);
        ret.add(item1);
        pot = new Potion(PotionType.INSTANT_HEAL);
        pot.setSplash(true);
        pot.apply(item1);
        ret.add(item1);
        ret.add(item1);
        pot = new Potion(PotionType.STRENGTH);
        pot.setHasExtendedDuration(true);
        pot.setSplash(true);
        pot.apply(item1);
        ret.add(item1);
        ret.add(item1);
        ret.add(new ItemStack(Material.LAVA_BUCKET,1));
        ret.add(new ItemStack(Material.WATER_BUCKET,1));
        ret.add(new ItemStack(Material.NETHER_WARTS,20));
        ret.add(new ItemStack(Material.REDSTONE_BLOCK,1));
        return ret;
    }

    public static List<ItemStack> getBadluckKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.WOOD_AXE,1));
        ret.add(new ItemStack(Material.WOOD_PICKAXE,1));
        ret.add(new ItemStack(Material.WOOD_HOE,1));
        ret.add(new ItemStack(Material.WOOD_SPADE,1));
        ret.add(new ItemStack(Material.COOKED_BEEF,10));
        ret.add(new ItemStack(Material.WOOD,64));
        return ret;
    }

    public static List<ItemStack> getBomberKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.TNT,10));
        ret.add(new ItemStack(Material.SAND,30));
        ret.add(new ItemStack(Material.SULPHUR,30));
        ret.add(new ItemStack(Material.IRON_PICKAXE,1));
        ret.add(new ItemStack(Material.REDSTONE_BLOCK,10));
        return ret;
    }

    public static List<ItemStack> getArcherKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.BOW,1));
        ret.add(new ItemStack(Material.ARROW,10));
        ret.add(new ItemStack(Material.LEATHER,30));
        ItemStack item = new ItemStack(Material.POTION, 1);
        Potion pot = new Potion(PotionType.STRENGTH);
        pot.setHasExtendedDuration(true);
        pot.setSplash(false);
        pot.apply(item);
        ret.add(item);
        return ret;
    }

    public static List<ItemStack> getSwordmanKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.IRON_SWORD,1));
        ret.add(new ItemStack(Material.IRON_BLOCK,1));
        return ret;
    }

    public static List<ItemStack> getRunnerKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ItemStack item1 = new ItemStack(Material.POTION, 1);
        Potion pot = new Potion(PotionType.POISON);
        pot.setHasExtendedDuration(false);
        pot.setSplash(true);
        pot.apply(item1);
        ret.add(item1);
        ItemStack item2 = new ItemStack(Material.POTION, 1);
        Potion pot2 = new Potion(PotionType.NIGHT_VISION);
        pot2.setHasExtendedDuration(true);
        pot2.setSplash(true);
        pot2.apply(item2);
        ret.add(item2);
        return ret;
    }

    public static List<ItemStack> getMinerKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ItemStack axe = new ItemStack(Material.STONE_AXE,1);
        axe.addEnchantment(Enchantment.DIG_SPEED, 4);
        ItemStack spade = new ItemStack(Material.STONE_SPADE,1);
        spade.addEnchantment(Enchantment.DIG_SPEED, 4);
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE,1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 4);
        ret.add(axe);
        ret.add(spade);
        ret.add(pickaxe);
        return ret;
    }

    public static List<ItemStack> getSwimerKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.WATER_BUCKET));
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET,1);
        helmet.addEnchantment(Enchantment.OXYGEN, 2);
        ret.add(helmet);
        return ret;
    }

    public static List<ItemStack> getFireManKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.COAL,10));
        ret.add(new ItemStack(Material.IRON_INGOT,10));
        ret.add(new ItemStack(Material.LAVA_BUCKET,1));
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET,1);
        helmet.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
        ret.add(helmet);
        return ret;
    }

    public static List<ItemStack> getMountaineerKit(){
        List<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Material.STONE_PICKAXE,1));
        ret.add(new ItemStack(Material.STRING,64));
        ret.add(new ItemStack(Material.FIREWORK_CHARGE,64));
        ret.add(new ItemStack(Material.SULPHUR,30));
        ret.add(new ItemStack(Material.SUGAR,30));
        return ret;
    }
}
