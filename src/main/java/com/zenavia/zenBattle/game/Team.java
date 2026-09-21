package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Team {
    private final String name;
    private final ConfigManager configManager;
    private final Set<UUID> players = new HashSet<>();
    private int beaconHealth;
    private boolean beaconAlive = true;
    private Location beaconLocation;

    public Team(String name, ConfigManager configManager) {
        this.name = name;
        this.configManager = configManager;
        this.beaconHealth = configManager.getSettings().beaconMaxHealth();
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public boolean isBeaconAlive() {
        return beaconAlive;
    }

    public void destroyBeacon() {
        this.beaconAlive = false;
    }

    public Location getBeaconLocation() {
        return beaconLocation != null ? beaconLocation.clone() : null;
    }

    public void setBeaconLocation(Location location) {
        this.beaconLocation = location;
    }

    public int getBeaconHealth() { return beaconHealth; }

    public List<Player> getOnlinePlayers() {
        return players.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean damageBeacon(int amount) {
        if (!beaconAlive) return false;
        beaconHealth -= amount;
        if (beaconHealth <= 0) {
            beaconAlive = false;
            return true;
        }
        return false;
    }

    public void reset(){
        players.clear();
        beaconAlive = true;
        beaconLocation = null;
        beaconHealth = configManager.getSettings().beaconMaxHealth();
    }
}
