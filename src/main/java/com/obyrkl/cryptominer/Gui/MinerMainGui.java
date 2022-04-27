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
            ItemStack cam = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta cammeta = cam.getItemMeta();
            cammeta.setDisplayName(" ");
            cam.setItemMeta(cammeta);
            inv.setItem(i,cam);
        }

        player.openInventory(inv);

        Main.instance.getMinerManager().setOpenMiner(player.getUniqueId(),miner);

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
        String base64 = MinerUtil.MinerSkull;
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
        List<String> lore = new ArrayList<>();
        if(miner.getDurability() > 0){
            item_0meta.setDisplayName(ChatUtil.format(Main.instance.getLang().getString("GUI.Miner.Name")));
            for(String text : Main.instance.getLang().getList("GUI.Miner.Lore")){
                text = text.replace("%status%",(miner.isMinerEnabled() == true) ? Main.instance.getLang().getString("GUI.enable") : Main.instance.getLang().getString("GUI.disable"));
                text = text.replace("%mine_value%",String.format("%.6f", miner.getMineValue()));
                text = text.replace("%total_mine_value%",String.format("%.6f", miner.getTotalMineValue()));

                String durability = null;
                if(miner.getDurability() >= 66.6){
                    durability = "&a"+String.format("%.2f", miner.getDurability())+"%";
                }else if(miner.getDurability() >= 33.3 && miner.getDurability() < 66.6){
                    durability = "&e"+String.format("%.2f", miner.getDurability())+"%";
                }else if(miner.getDurability() < 33.3){
                    durability = "&c"+String.format("%.2f", miner.getDurability())+"%";
                }

                text = text.replace("%durability%",durability);
                text = text.replace("%change_status%",((miner.isMinerEnabled() == true) ? Main.instance.getLang().getString("GUI.close") : Main.instance.getLang().getString("GUI.open")));
                lore.add(ChatUtil.format(text));
            }
        }else{
            item_0meta.setDisplayName(ChatUtil.format(Main.instance.getLang().getString("GUI.Broken-Miner.Name")));
            for(String text : Main.instance.getLang().getList("GUI.Broken-Miner.Lore")){
                text = text.replace("%status%",(miner.isMinerEnabled() == true) ? Main.instance.getLang().getString("GUI.enable") : Main.instance.getLang().getString("GUI.disable"));
                text = text.replace("%mine_value%",String.format("%.6f", miner.getMineValue()));
                text = text.replace("%total_mine_value%",String.format("%.6f", miner.getTotalMineValue()));

                String durability = null;
                if(miner.getDurability() >= 66.6){
                    durability = "&a"+String.format("%.2f", miner.getDurability())+"%";
                }else if(miner.getDurability() >= 33.3 && miner.getDurability() < 66.6){
                    durability = "&e"+String.format("%.2f", miner.getDurability())+"%";
                }else if(miner.getDurability() < 33.3){
                    durability = "&c"+String.format("%.2f", miner.getDurability())+"%";
                }

                text = text.replace("%durability%",durability);
                text = text.replace("%repair_cost%",String.format("%.2f", Main.instance.getConfig().getDouble("Miner.repairCost")));
                text = text.replace("%change_status%",((miner.isMinerEnabled() == true) ? Main.instance.getLang().getString("GUI.close") : Main.instance.getLang().getString("GUI.open")));
                lore.add(ChatUtil.format(text));
            }
        }
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
        item_1meta.setDisplayName(ChatUtil.format(Main.instance.getLang().getString("GUI.Information.Name")));
        List<String> lore1 = new ArrayList<>();
        for(String text : Main.instance.getLang().getList("GUI.Information.Lore")){
            text = text.replace("%miner_balance%",String.format("%.6f", miner.getMinerBalance()));
            text = text.replace("%balance_value%",String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice()*miner.getMinerBalance()));
            lore1.add(ChatUtil.format(text));
        }
        item_1meta.setLore(lore1);
        item_1.setItemMeta(item_1meta);

        inv.setItem(16,item_1);


        ItemStack item_2 = new ItemStack(Material.PLAYER_HEAD, 1,(short) 3);
        SkullMeta item_2meta = (SkullMeta) item_0.getItemMeta();
        GameProfile profile2 = new GameProfile(UUID.randomUUID(), null);
        profile2.getProperties().put("textures", new Property("textures", base64));
        Field profileField2 = null;
        try {
            profileField2 = item_2meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField2.setAccessible(true);
        try {
            profileField2.set(item_1meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item_2meta.setDisplayName(ChatUtil.format(Main.instance.getLang().getString("GUI.Market.Name")));
        List<String> lore2 = new ArrayList<>();
        for(String text : Main.instance.getLang().getList("GUI.Market.Lore")){
            text = text.replace("%price%",String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice()));
            text = text.replace("%price_change_24h%",(Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_24h() < 0) ? ChatUtil.format("&c"+String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_24h())) : ChatUtil.format("&a"+String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_24h())));
            text = text.replace("%price_change_percentage_24h%",(Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_percentage_24h() < 0) ? ChatUtil.format("&c"+String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_percentage_24h())) : ChatUtil.format("&a"+String.format("%.2f", Main.instance.getCryptoManager().getCrypto("Bitcoin").getPrice_change_percentage_24h())));
            lore2.add(ChatUtil.format(text));
        }
        item_2meta.setLore(lore2);
        item_2.setItemMeta(item_2meta);

        inv.setItem(13,item_2);
    }

}
