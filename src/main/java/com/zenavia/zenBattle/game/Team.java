package com.zenavia.zenBattle.game;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {
    private final String name;
    private final Set<UUID> players = new HashSet<>();
    private boolean beaconAlive = true;
    private Location beaconLocation;

    public Team(String name) {
        this.name = name;
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

    public void reset(){
        players.clear();
        beaconAlive = true;
        beaconLocation = null;
    }
}
