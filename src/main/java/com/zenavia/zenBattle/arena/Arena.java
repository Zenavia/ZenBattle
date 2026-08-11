package com.zenavia.zenBattle.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class Arena {
    // définition statique d'une map
    private final String name;
    private final Location spawnTeamA;
    private final Location spawnTeamB;
    private final Location beaconTeamA;
    private final Location beaconTeamB;
    private final Location lobbySpawn;
    private final Location barrierCorner1;
    private final Location barrierCorner2;
    private final Material barrierMaterial;
    private final List<Location> spawnPointsTeamA;
    private final List<Location> spawnPointsTeamB;

    public Arena(String name, Location spawnTeamA, Location spawnTeamB,
                 Location beaconTeamA, Location beaconTeamB, Location lobbySpawn, Location barrierCorner1, Location barrierCorner2, Material barrierMaterial, List<Location> spawnPointsTeamA, List<Location> spawnPointsTeamB) {
        this.name = name;
        this.spawnTeamA = spawnTeamA;
        this.spawnTeamB = spawnTeamB;
        this.beaconTeamA = beaconTeamA;
        this.beaconTeamB = beaconTeamB;
        this.lobbySpawn = lobbySpawn;
        this.barrierCorner1 = barrierCorner1;
        this.barrierCorner2 = barrierCorner2;
        this.barrierMaterial = barrierMaterial;
        this.spawnPointsTeamA = spawnPointsTeamA;
        this.spawnPointsTeamB = spawnPointsTeamB;
    }

    public String getName() {
        return name;
    }

    public Location getSpawnTeamA() {
        return spawnTeamA.clone();
    }

    public Location getSpawnTeamB() {
        return spawnTeamB.clone();
    }

    public Location getBeaconTeamA() {
        return beaconTeamA.clone();
    }

    public Location getBeaconTeamB() {
        return beaconTeamB.clone();
    }

    public Location getLobbySpawn() {
        return lobbySpawn.clone();
    }

    public Location getBarrierCorner1() { return barrierCorner1 != null ? barrierCorner1.clone() : null; }
    public Location getBarrierCorner2() { return barrierCorner2 != null ? barrierCorner2.clone() : null; }
    public Material getBarrierMaterial() { return barrierMaterial; }
    public List<Location> getSpawnPointsTeamA() { return spawnPointsTeamA; }
    public List<Location> getSpawnPointsTeamB() { return spawnPointsTeamB; }
}
