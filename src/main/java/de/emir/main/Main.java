package de.emir.main;

import de.emir.sql.MySQL;
import de.emir.utils.Config;
import de.emir.utils.GameState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main {
    private static Main instance;

    private static GameState state;

    public static boolean newmap = false;

    public static String prefix;

    private static boolean setup = false;

    public static MySQL mysql;

    public static ArrayList<Location> blocklist = new ArrayList<>();

    public static ArrayList<World> worldlist = new ArrayList<>();

    public static ArrayList<Player> build = new ArrayList<>();

    public static HashMap<Player, String> sortierung = new HashMap<>();

    public static HashMap<String, Location> locs = new HashMap<>();

    public void onEnable(){
        instance = this;
        Config.create();
        prefix = "§7 > §bMLGRUSH §8| §7";
        state = GameState.LOBBY;
        newmap = false;
        Bukkit.getPluginManager().registerEvents((Listener)new SetupListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new JoinListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new EnvironmentListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new DamageListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new QuitListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new HeightListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ItemListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new LoginListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents(this, (Plugin)this);
        Bukkit.getPluginCommand("start").setExecutor((CommandExecutor)new CMD_forcestart());
        Bukkit.getPluginCommand("build").setExecutor((CommandExecutor)new CMD_build());
        Bukkit.getPluginCommand("stats").setExecutor((CommandExecutor)new CMD_stats());
        setup = (new File("plugins//MLGRush", "locations.yml")).exists();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        createMySQLConfig();
        ConnctMySQL();
    }

    public void onDisable() {
        for (Player all : Bukkit.getOnlinePlayers())
            all.kickPlayer(" ");
        for (int i = 0; i < blocklist.size(); i++)
            ((World)worldlist.get(i)).getBlockAt(blocklist.get(i)).setType(Material.AIR);
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (getState() == GameState.LOBBY) {
            e.setMaxPlayers(2);
        } else {
            e.setMaxPlayers(8);
        }
    }

    public static String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&', Config.cfg.getString(path));
    }

    public static Main getInstance() {
        return instance;
    }

    public static GameState getState() {
        return state;
    }

    public static boolean getSetupState() {
        return setup;
    }

    public void createMySQLConfig() {
        File f = new File("plugins//MLGRush", "mysql.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            cfg.set("MySQL.ip", "localhost");
            cfg.set("MySQL.database", "MLGRushStats");
            cfg.set("MySQL.name", "root");
            cfg.set("MySQL.password", "password");
            try {
                cfg.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ConnctMySQL() {
        File f = new File("plugins//MLGRush", "mysql.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        String ip = cfg.getString("MySQL.ip");
        String database = cfg.getString("MySQL.database");
        String name = cfg.getString("MySQL.name");
        String passwort = cfg.getString("MySQL.password");
        try {
            mysql = new MySQL(ip, database, name, passwort);
            mysql.update("CREATE TABLE IF NOT EXISTS StatsAPI(UUID varchar(64), PLAY int, WINS int, BREAK int, POINTS int);");
        } catch (Exception error) {
            Bukkit.getConsoleSender().sendMessage("MySQL konnte nicht verbunden werden");
        }
    }

    public static void setState(GameState newstate) {
        state = newstate;
    }
}