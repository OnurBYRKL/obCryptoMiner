package com.obyrkl.cryptominer.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class MinerUtil {

    public static String MinerSkull = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNkNzBjZTQ4MTg1ODFjYTQ3YWRmNmI4MTY3OWZkMTY0NmZkNjg3YzcxMjdmZGFhZTk0ZmVkNjQwMTU1ZSJ9fX0=";

    public static ItemStack getMinerItem(@Nullable UUID uuid) {
        String base64 = MinerSkull;
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
            Miner miner = Main.instance.getMinerManager().getMiner(uuid);
            String durability = null;
            if(miner.getDurability() >= 66.6){
                durability = "&a"+String.format("%.2f", miner.getDurability())+"%";
            }else if(miner.getDurability() >= 33.3 && miner.getDurability() < 66.6){
                durability = "&e"+String.format("%.2f", miner.getDurability())+"%";
            }else if(miner.getDurability() < 33.3){
                durability = "&c"+String.format("%.2f", miner.getDurability())+"%";
            }
            String owner = (Bukkit.getPlayer(miner.getOwner()) == null) ? Bukkit.getOfflinePlayer(miner.getOwner()).getName() : Bukkit.getPlayer(miner.getOwner()).getName();
            lore.add(ChatUtil.format("&7Sahip: &8"+owner));
            lore.add(ChatUtil.format("&7Üretim: &8"+String.format("%.6f", miner.getMineValue())+" BTC"));
            lore.add(ChatUtil.format("&7Toplam Üretim: &8"+String.format("%.6f", miner.getTotalMineValue())+" BTC"));
            lore.add(ChatUtil.format("&7Miner Bakiyesi: &8"+String.format("%.6f", miner.getMinerBalance())+" BTC"));
            lore.add(ChatUtil.format("&7Dayanıklılık: "+durability));
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

    public static HashMap<String,String> getCurrency(String coin, String currency) throws IOException, InterruptedException {
        HashMap<String, String> currencyMap = new HashMap<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            JsonParser parser = new JsonParser();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coingecko.com/api/v3/coins/markets?vs_currency="+currency+"&ids="+coin.toLowerCase()+"&order=market_cap_desc&per_page=1&page=1&sparkline=false"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body().replace("[","");
            responseBody = responseBody.replace("]","");
            JsonObject obj = new JsonObject();
            obj = parser.parse(responseBody).getAsJsonObject();
            currencyMap.put("current_price", String.valueOf(obj.get("current_price").getAsFloat()));
            currencyMap.put("price_change_24h", String.valueOf(obj.get("price_change_24h").getAsFloat()));
            currencyMap.put("price_change_percentage_24h", String.valueOf(obj.get("price_change_percentage_24h").getAsFloat()));
            return currencyMap;
        } catch (Exception e) {
            e.printStackTrace();
            return currencyMap;
        }
    }

    public static boolean isInteger(String s)
    {
        int intValue;
        try {
            intValue = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s)
    {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
