package com.zenavia.zenBattle.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
        plugin.saveDefaultConfig();
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
        Location corner1 = readLocation(yaml, "barrier-corner-1");
        Location corner2 = readLocation(yaml, "barrier-corner-2");
        Material barrierMaterial = Material.TINTED_GLASS;
        String materialName = yaml.getString("barrier-material");
        List<Location> spawnPointsA = scanSpawnPoints(yaml, "spawn-zone-team-a-corner-1", "spawn-zone-team-a-corner-2", "spawn-marker-team-a");
        List<Location> spawnPointsB = scanSpawnPoints(yaml, "spawn-zone-team-b-corner-1", "spawn-zone-team-b-corner-2", "spawn-marker-team-b");

        if (materialName != null) {
            try {
                barrierMaterial = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Matériau de barrière invalide dans " + file.getName() + " : " + materialName);
            }
        }

        if (spawnA == null || spawnB == null || beaconA == null || beaconB == null || lobby == null) {
            plugin.getLogger().log(Level.WARNING, "Arène invalide, coordonnées manquantes : " + file.getName());
            return null;
        }

        return new ArenaConfig(name, spawnA, spawnB, beaconA, beaconB, lobby, corner1, corner2, barrierMaterial, spawnPointsA, spawnPointsB);
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

    private List<Location> scanSpawnPoints(YamlConfiguration yaml, String cornerKey1, String cornerKey2, String markerKey) {
        List<Location> found = new ArrayList<>();

        Location corner1 = readLocation(yaml, cornerKey1);
        Location corner2 = readLocation(yaml, cornerKey2);
        String materialName = yaml.getString(markerKey);
        if (corner1 == null || corner2 == null || materialName == null) return found;

        Material marker;
        try {
            marker = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Matériau de spawn invalide : " + materialName);
            return found;
        }

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        World world = corner1.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == marker) {
                        found.add(block.getLocation().add(0.5, 1, 0.5));
                    }
                }
            }
        }

        plugin.getLogger().info(found.size() + " points de spawn trouvés pour le marqueur " + marker);
        return found;
    }

    public void reload() {
        loadAll();
    }

    public void setMinPlayersToStart(int value) {
        this.settings = settings.withMinPlayersToStart(value);
        plugin.getConfig().set("game.min-players-to-start", value);
        plugin.saveConfig();
    }

    public void setBeaconMaxHealth(int value) {
        this.settings = settings.withBeaconMaxHealth(value);
        plugin.getConfig().set("game.beacon-max-health", value);
        plugin.saveConfig();
    }

    public void setDamagePerHit(int value) {
        this.settings = settings.withDamagePerHit(value);
        plugin.getConfig().set("game.damage-per-hit", value);
        plugin.saveConfig();
    }
}
