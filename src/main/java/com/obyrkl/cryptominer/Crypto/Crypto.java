package com.obyrkl.cryptominer.Crypto;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class Crypto {

    private String name;
    private String shortName;
    private double price;
    private double price_change_24h;
    private double price_change_percentage_24h;

    private BukkitTask cryptoTask;

    public Crypto(String name, String shortName, double price, double price_change_24h, double price_change_percentage_24h){
        this.name = name;
        this.shortName = shortName;
        this.price = price;
        startPriceCheck();
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public double getPrice() {
        return price;
    }

    public double getPrice_change_24h() {
        return price_change_24h;
    }

    public double getPrice_change_percentage_24h() {
        return price_change_percentage_24h;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void startPriceCheck() {
        this.cryptoTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> currencyMap = MinerUtil.getCurrency(name,Main.instance.getConfig().getString("Miner.cryptocurrency"));
                    price = Float.valueOf(currencyMap.get("current_price"));
                    price_change_24h = Float.valueOf(currencyMap.get("price_change_24h"));
                    price_change_percentage_24h = Float.valueOf(currencyMap.get("price_change_percentage_24h"));
                } catch (IOException | InterruptedException e) {
                    Main.instance.getLogger().log(Level.SEVERE,"An error occurred while creating crypto!");
                    return;
                }
            }
        }.runTaskTimer(Main.instance, 20L * 10, 20L * 10);
    }
}
