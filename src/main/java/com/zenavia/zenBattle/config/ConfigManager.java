package com.zenavia.zenBattle.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    private final Plugin plugin;
    private GameSettings settings;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        plugin.saveDefaultConfig(); // copie config.yml des resources si absent
        plugin.reloadConfig();

        var config = plugin.getConfig();
        this.settings = new GameSettings(
                config.getInt("game.min-players-to-start", 2),
                config.getInt("game.countdown-seconds", 10),
                config.getInt("game.end-delay-seconds", 5),
                config.getInt("game.beacon-max-health", 10),
                config.getInt("game.damage-per-hit", 1),
                config.getString("game.first-team", "Rouge"),
                config.getString("game.second-team", "Bleu")
        );
    }

    public GameSettings getSettings() {
        return settings;
    }

    public List<ArenaConfig> loadArenas() {
        List<ArenaConfig> arenas = new ArrayList<>();
        File arenasDir = new File(plugin.getDataFolder(), "arenas");
        if (!arenasDir.exists()) arenasDir.mkdirs();

        File[] files = arenasDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return arenas;

        for (File file : files) {
            ArenaConfig arena = loadArenaFile(file);
            if (arena != null) arenas.add(arena);
        }
        return arenas;
    }

    private ArenaConfig loadArenaFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String name = yaml.getString("name", file.getName().replace(".yml", ""));

        Location spawnA = readLocation(yaml, "spawn-team-a");
        Location spawnB = readLocation(yaml, "spawn-team-b");
        Location beaconA = readLocation(yaml, "beacon-team-a");
        Location beaconB = readLocation(yaml, "beacon-team-b");
        Location lobby = readLocation(yaml, "lobby-spawn");

        if (spawnA == null || spawnB == null || beaconA == null || beaconB == null || lobby == null) {
            plugin.getLogger().log(Level.WARNING, "Arène invalide, coordonnées manquantes : " + file.getName());
            return null;
        }

        return new ArenaConfig(name, spawnA, spawnB, beaconA, beaconB, lobby);
    }

    private Location readLocation(YamlConfiguration yaml, String path) {
        if (!yaml.contains(path)) return null;
        String worldName = yaml.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().log(Level.WARNING, "Monde introuvable pour '" + path + "' : " + worldName);
            return null;
        }
        double x = yaml.getDouble(path + ".x");
        double y = yaml.getDouble(path + ".y");
        double z = yaml.getDouble(path + ".z");
        float yaw = (float) yaml.getDouble(path + ".yaw", 0);
        float pitch = (float) yaml.getDouble(path + ".pitch", 0);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
