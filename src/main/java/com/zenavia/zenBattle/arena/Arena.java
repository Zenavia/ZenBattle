package com.zenavia.zenBattle.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

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

    public Arena(String name, Location spawnTeamA, Location spawnTeamB,
                 Location beaconTeamA, Location beaconTeamB, Location lobbySpawn, Location barrierCorner1, Location barrierCorner2, Material barrierMaterial) {
        this.name = name;
        this.spawnTeamA = spawnTeamA;
        this.spawnTeamB = spawnTeamB;
        this.beaconTeamA = beaconTeamA;
        this.beaconTeamB = beaconTeamB;
        this.lobbySpawn = lobbySpawn;
        this.barrierCorner1 = barrierCorner1;
        this.barrierCorner2 = barrierCorner2;
        this.barrierMaterial = barrierMaterial;
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

    public static Arena createDefault() {
        World world = Bukkit.getWorld("flat");
        return new Arena(
                "arena1",
                new Location(world, 19, -60, 19),   // spawn équipe A
                new Location(world, 19, -60, 63),  // spawn équipe B
                new Location(world, 19, -59, 8),    // beacon équipe A
                new Location(world, 19, -59, 75),   // beacon équipe B
                new Location(world, -2, -60, 63),      // spawn lobby
                new Location(world, 0, -60, 0),   // coin barrière 1
                new Location(world, 38, -60, 75), // coin barrière 2
                Material.TINTED_GLASS);
    }
}
