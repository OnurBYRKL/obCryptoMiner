package com.obyrkl.cryptominer.Utils;

import com.obyrkl.cryptominer.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Lang {

    private Main m;

    private File LangStorageFile;
    private FileConfiguration LangStorageConfig;

    public Lang(Main plugin){
        this.m = plugin;
        createLang();
    }

    public void createLang(){
        LangStorageFile = new File(this.m.getDataFolder(), "lang.yml");
        if(!LangStorageFile.exists()){
            LangStorageFile.getParentFile().mkdirs();
            this.m.saveResource("lang.yml", true);
        }

        LangStorageConfig = new YamlConfiguration();
        try {
            LangStorageConfig.load(LangStorageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key){
        return LangStorageConfig.getString(key);
    }

    public int getInt(String key){
        return LangStorageConfig.getInt(key);
    }

    public List<String> getList(String key){
        return LangStorageConfig.getStringList(key);
    }
}
