package com.obyrkl.cryptominer.Gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.ChatUtil;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinerMainGui {

    public static void MinerMainGui(Player player, Miner miner) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatUtil.format("&0Miner"));

        for(int i=0;i<27;i++){
            inv.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        player.openInventory(inv);

        new BukkitRunnable() {
            @Override
            public void run(){
                if(!player.isOnline() || !player.getOpenInventory().getTitle().equalsIgnoreCase(ChatUtil.format("&0Miner")) || player.getOpenInventory().getTopInventory() != inv){
                    this.cancel();
                    return;
                }
                init(inv,miner);
            }
        }.runTaskTimer(Main.instance, 0L, 20L);
    }

    private static void init(Inventory inv, Miner miner){
        String base64 = Main.instance.MinerSkull;
        ItemStack item_0 = new ItemStack(Material.PLAYER_HEAD, 1,(short) 3);
        SkullMeta item_0meta = (SkullMeta) item_0.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField = null;
        try {
            profileField = item_0meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(item_0meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item_0meta.setDisplayName(ChatUtil.format("&dMiner"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatUtil.format("&7Üretim;"));
        lore.add(ChatUtil.format("  &e"+String.format("%.6f", miner.getMineValue())+" BTC/d"));
        lore.add(ChatUtil.format(" "));
        lore.add(ChatUtil.format("&7Toplam Üretim;"));
        lore.add(ChatUtil.format("  &e"+String.format("%.6f", miner.getTotalMineValue())+" BTC"));
        item_0meta.setLore(lore);
        item_0.setItemMeta(item_0meta);

        inv.setItem(10,item_0);

        ItemStack item_1 = new ItemStack(Material.PLAYER_HEAD, 1,(short) 3);
        SkullMeta item_1meta = (SkullMeta) item_0.getItemMeta();
        GameProfile profile1 = new GameProfile(UUID.randomUUID(), null);
        profile1.getProperties().put("textures", new Property("textures", base64));
        Field profileField1 = null;
        try {
            profileField1 = item_1meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField1.setAccessible(true);
        try {
            profileField1.set(item_1meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item_1meta.setDisplayName(ChatUtil.format("&dBilgi"));
        List<String> lore1 = new ArrayList<>();
        lore1.add(ChatUtil.format("&7Miner Bakiyesi;"));
        lore1.add(ChatUtil.format("  &e"+String.format("%.6f", miner.getMinerBalance())+" BTC"));
        item_1meta.setLore(lore1);
        item_1.setItemMeta(item_1meta);

        inv.setItem(16,item_1);
    }

}
