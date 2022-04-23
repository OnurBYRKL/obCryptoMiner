package com.obyrkl.cryptominer.Miner;

import com.obyrkl.cryptominer.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Miner {

    private UUID uuid;
    private UUID owner;
    private double mineValue;
    private double totalMineValue;
    private double minerBalance;
    private Location location;
    private boolean placed;

    private BukkitTask miningTask;

    public Miner(UUID uuid, UUID owner, double mineValue, double totalMineValue, double minerBalance, Location location, boolean placed){
        this.uuid = uuid;
        this.owner = owner;
        this.mineValue = mineValue;
        this.totalMineValue = totalMineValue;
        this.minerBalance = minerBalance;
        this.location = location;
        this.placed = placed;
        if(placed == true){
            startMining();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getOwner() {
        return owner;
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
        placed = val;
        if(placed == true){
            startMining();
        }
    }

    public void startMining(){
        this.miningTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(placed == false){
                    return;
                }
                totalMineValue+=mineValue;
                minerBalance+=mineValue;
                System.out.println("add "+String.format("%.6f", mineValue)+" btc to "+uuid);
            }
        }.runTaskTimer(Main.instance, 20L * 10, 20L * 10);
    }



}
