package com.obyrkl.cryptominer.Miner;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;

public class MinerManager {

    private Main plugin;

    private Set<Miner> minersSet;

    public MinerManager(Main plugin){
        this.plugin = plugin;
        this.minersSet = new LinkedHashSet<>();
    }

    public void createMiner(UUID owner, UUID miner){
        if(getMiner(miner) == null){
            addMiner(new Miner(miner,owner,0.00005,0.0,0.0,new Location(Bukkit.getWorld("world"),0.0,0.0,0.0,0,0),false));
        }
    }

    public void addMiner(Miner miner){
        minersSet.add(miner);
    }

    public void removeMiner(Miner miner){
        minersSet.remove(miner);
    }

    public Miner getMiner(UUID uuid){
        return minersSet.stream().filter(miner -> miner.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public Miner getMiner(Location location){
        return minersSet.stream().filter(miner -> miner.getLocation().equals(location)).findFirst().orElse(null);
    }

    public Set<Miner> getMiners() {
        return minersSet;
    }

}
