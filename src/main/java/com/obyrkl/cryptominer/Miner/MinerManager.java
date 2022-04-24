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
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class MinerManager {

    private Main plugin;

    private Set<Miner> minersSet;
    private HashMap<UUID, Miner> openMiner;

    public MinerManager(Main plugin){
        this.plugin = plugin;
        this.minersSet = new LinkedHashSet<>();
        this.openMiner = new HashMap<>();
    }

    public void createMiner(UUID owner, UUID miner){
        if(getMiner(miner) == null){
            addMiner(new Miner(
                    miner,
                    owner,
                    Main.instance.getConfig().getDouble("Miner.perMinuteMine"),
                    0.0,
                    0.0,
                    100.0,
                    new Location(Bukkit.getWorld("world"),0.0,0.0,0.0,0,0),
                    false,
                    false,
                    System.currentTimeMillis(),
                    Main.instance.getConfig().getDouble("Miner.damageRange.min"),
                    Main.instance.getConfig().getDouble("Miner.damageRange.max")
            ));
        }
    }

    public void addMiner(Miner miner){
        minersSet.add(miner);
    }

    public void removeMiner(Miner miner){
        try {
            plugin.getDatabase().deleteMiner(miner);
            minersSet.remove(miner);
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE,"["+miner.getUUID()+"] An error occurred while deleting the miner");
        }
    }

    public void setOpenMiner(UUID uuid, Miner miner){
        if(openMiner.containsKey(uuid)){
            this.openMiner.replace(uuid,miner);
        }else{
            this.openMiner.put(uuid,miner);
        }
    }

    public void minerClosed(UUID uuid){
        this.openMiner.remove(uuid);
    }

    public boolean hasOpenedMiner(UUID uuid) {
        return this.openMiner.containsKey(uuid);
    }

    public Miner getOpenedMiner(UUID uuid) {
        return this.openMiner.get(uuid);
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
