package com.obyrkl.cryptominer.Utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.obyrkl.cryptominer.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinerUtil {

    public static ItemStack getMinerItem(@Nullable UUID uuid) {
        String base64 = Main.instance.MinerSkull;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1,(short) 3);
        if (base64 == null || base64.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skullMeta.setDisplayName(ChatUtil.format("&eMiner"));
        if(uuid != null){
            List<String> lore = new ArrayList<>();
            lore.add(ChatUtil.format("&7UUID: &8"+uuid));
            skullMeta.setLore(lore);
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static UUID createRandomUUID(){
        UUID uuid = UUID.fromString(UUID.randomUUID().toString().substring(0, 36));
        while (Main.instance.getMinerManager().getMiner(uuid) != null){
            uuid = UUID.fromString(UUID.randomUUID().toString().substring(0, 36));
        }
        return uuid;
    }

}
