package com.obyrkl.cryptominer.Crypto;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class CryptoManager {

    private Main plugin;

    private Set<Crypto> cryptoSet;

    public CryptoManager(Main plugin){
        this.plugin = plugin;
        this.cryptoSet = new LinkedHashSet<>();
    }


    public void addCrypto(String name, String shortName){
        try {
            HashMap<String, String> currencyMap = MinerUtil.getCurrency(name,Main.instance.getConfig().getString("Miner.cryptocurrency"));
            cryptoSet.add(new Crypto(name,shortName,Float.valueOf(currencyMap.get("current_price")),Float.valueOf(currencyMap.get("price_change_24h")),Float.valueOf(currencyMap.get("price_change_percentage_24h"))));
        } catch (IOException | InterruptedException e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred while creating crypto!");
        }
    }

    public void removeCrypto(Crypto crypto){
        cryptoSet.remove(crypto);
    }

    public Crypto getCrypto(String name){
        return cryptoSet.stream().filter(crypto -> crypto.getName().equals(name)).findFirst().orElse(null);
    }

    public Set<Crypto> getCryptoList() {
        return cryptoSet;
    }

}
