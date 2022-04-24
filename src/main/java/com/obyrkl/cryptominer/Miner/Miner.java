package com.obyrkl.cryptominer.Miner;

import com.obyrkl.cryptominer.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Miner {

    private UUID uuid;
    private UUID owner;
    private double mineValue;
    private double totalMineValue;
    private double minerBalance;
    private double durability;
    private Location location;
    private boolean minerStatus;
    private boolean placed;
    private long lastUsed;
    private double minDamage;
    private double maxDamage;

    private BukkitTask miningTask;
    private BukkitTask playerCheckTask;
    private BukkitTask lastUsedCheckTask;

    public Miner(UUID uuid, UUID owner, double mineValue, double totalMineValue, double minerBalance, double durability, Location location, boolean minerStatus, boolean placed, long lastUsed, double minDamage, double maxDamage){
        this.uuid = uuid;
        this.owner = owner;
        this.mineValue = mineValue;
        this.totalMineValue = totalMineValue;
        this.minerBalance = minerBalance;
        this.durability = durability;
        this.location = location;
        this.minerStatus = minerStatus;
        this.placed = placed;
        this.lastUsed = lastUsed;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        startMining();
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public double getMineValue() {
        return mineValue;
    }

    public double getTotalMineValue() {
        return totalMineValue;
    }

    public double getMinerBalance() {
        return minerBalance;
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
        this.durability = durability;
    }

    public void setBalance(double minerBalance) {
        this.minerBalance = minerBalance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean val){
        if(miningTask != null){ miningTask.cancel(); }
        if(playerCheckTask != null){ playerCheckTask.cancel(); }
        placed = val;
        if(placed == true){
            startMining();
        }
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public boolean isMinerStatus() {
        return minerStatus;
    }


    public boolean isMinerEnabled() {
        return minerStatus;
    }

    public void setMinerStatus(boolean val) {
        if(miningTask != null){ miningTask.cancel(); }
        if(playerCheckTask != null){ playerCheckTask.cancel(); }
        minerStatus = val;
        if(minerStatus == true){
            startMining();
        }
    }

    public void startMining(){
        this.miningTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(placed == false || minerStatus == false){
                    this.cancel();
                    return;
                }
                double value = (Math.random() * (maxDamage - minDamage) + minDamage);
                if((durability-value) > 0){
                    durability-=value;
                    totalMineValue+=mineValue;
                    minerBalance+=mineValue;
                    if(durability < 0){
                        durability = 0;
                        minerStatus = false;
                        location.getWorld().createExplosion(location.getX(),location.getY(),location.getZ(),1F,false,false);
                        this.cancel();
                        return;
                    }
                }else{
                    durability = 0;
                    totalMineValue+=mineValue;
                    minerBalance+=mineValue;
                    minerStatus = false;
                    location.getWorld().createExplosion(location.getX(),location.getY(),location.getZ(),1F,false,false);
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(Main.instance, 20L * 60, 20L * 60);

        int checkPlayerMinute = Main.instance.getConfig().getInt("Miner.checkPlayerMinute");

        this.playerCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(placed == false || minerStatus == false){
                    this.cancel();
                    return;
                }
                int checkPlayerRadius = Main.instance.getConfig().getInt("Miner.checkPlayerRadius");
                List<Entity> nearbyEntites = location.getWorld().getNearbyEntities(location,checkPlayerRadius,checkPlayerRadius,checkPlayerRadius).stream().filter(entity -> entity.getType().equals(EntityType.PLAYER)).collect(Collectors.toList());
                if(nearbyEntites.size() < 1){
                    minerStatus = false;
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(Main.instance, 20L * (checkPlayerMinute*60), 20L * (checkPlayerMinute*60));

        if(this.lastUsedCheckTask == null){
            this.lastUsedCheckTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if(placed == true){
                        lastUsed = System.currentTimeMillis();
                    }
                    if(Main.instance.getConfig().getBoolean("Miner.delete-unused-Miners.Status") == true){
                        long timeLeft = System.currentTimeMillis() - lastUsed;
                        if(TimeUnit.MILLISECONDS.toDays(timeLeft) >= Main.instance.getConfig().getInt("Miner.delete-unused-Miners.Time")){
                            Main.instance.getLogger().log(Level.INFO,"["+uuid+"] expired and deleted!");
                            Main.instance.getMinerManager().removeMiner(Miner.this);
                            this.cancel();
                            return;
                        }
                    }
                }
            }.runTaskTimer(Main.instance, 20L * (30*60), 20L * (30*60));
        }
    }



}
