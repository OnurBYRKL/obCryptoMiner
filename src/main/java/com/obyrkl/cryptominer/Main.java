package com.obyrkl.cryptominer;

import com.obyrkl.cryptominer.Commands.MainCommand;
import com.obyrkl.cryptominer.Listeners.MinerListeners;
import com.obyrkl.cryptominer.Miner.MinerManager;
import com.obyrkl.cryptominer.Utils.Database;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main instance;

    private Database database;
    private MinerManager minerManager;

    public String MinerSkull = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNkNzBjZTQ4MTg1ODFjYTQ3YWRmNmI4MTY3OWZkMTY0NmZkNjg3YzcxMjdmZGFhZTk0ZmVkNjQwMTU1ZSJ9fX0=";

    @Override
    public void onEnable() {
        this.instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new MinerListeners(),this);

        getCommand("obcrypto").setExecutor(new MainCommand());

        this.minerManager = new MinerManager(this);

        try {
            this.database = new Database(this);
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().log(Level.SEVERE, "SQL connection problem found!");
        }



    }

    @Override
    public void onDisable() {
        try {
            database.saveMiners();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        instance = null;
    }

    public Database getDatabase() {
        return database;
    }

    public MinerManager getMinerManager() {
        return minerManager;
    }
}
