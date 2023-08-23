package de.emir.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
    public static File file = new File("plugins//MLGRush", "config.yml");
    public static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static void create(){
        if (!file.exists()){

        }
    }
}
