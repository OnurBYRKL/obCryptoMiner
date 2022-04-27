package com.obyrkl.cryptominer;

import com.obyrkl.cryptominer.Commands.MainCommand;
import com.obyrkl.cryptominer.Crypto.CryptoManager;
import com.obyrkl.cryptominer.Listeners.Gui.MainGuiListener;
import com.obyrkl.cryptominer.Listeners.MinerListeners;
import com.obyrkl.cryptominer.Listeners.PlayerListeners;
import com.obyrkl.cryptominer.Miner.MinerManager;
import com.obyrkl.cryptominer.Utils.Database;
import com.obyrkl.cryptominer.Utils.Lang;
import com.obyrkl.cryptominer.Utils.Metrics;
import com.obyrkl.cryptominer.Utils.MinerUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Main instance;

    private Database database;
    private MinerManager minerManager;
    private CryptoManager cryptoManager;

    private Lang lang;

    private Economy econ;

    @Override
    public void onEnable() {
        this.instance = this;

        //Config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Main Listeners
        Bukkit.getPluginManager().registerEvents(new MinerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);

        //Gui Listeners
        Bukkit.getPluginManager().registerEvents(new MainGuiListener(), this);

        //Commands
        getCommand("obcrypto").setExecutor(new MainCommand());

        //Managers
        this.minerManager = new MinerManager(this);
        this.cryptoManager = new CryptoManager(this);
        cryptoManager.addCrypto("Bitcoin","BTC");

        //Lang
        this.lang = new Lang(this);

        //Database
        try {
            this.database = new Database(this);
        } catch (SQLException | ClassNotFoundException e) {
            getLogger().log(Level.SEVERE, "SQL connection problem found!");
        }

        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE, "Vault hook not found!");
            return;
        }
        getLogger().log(Level.INFO, "Vault hook found!");

        Metrics metrics = new Metrics(this, 15040);
        metrics.addCustomChart(new Metrics.SingleLineChart("miner_sayisi", () -> minerManager.getMiners().size()));

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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = (Economy)rsp.getProvider();
        return (econ != null);
    }

    public Economy getEconomy() {
        return econ;
    }

    public Database getDatabase() {
        return database;
    }

    public MinerManager getMinerManager() {
        return minerManager;
    }

    public CryptoManager getCryptoManager() {
        return cryptoManager;
    }

    public Lang getLang() {
        return lang;
    }
}
