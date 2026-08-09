package com.zenavia.zenBattle.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Arena {
    // définition statique d'une map
    private final String name;
    private final Location spawnTeamA;
    private final Location spawnTeamB;
    private final Location beaconTeamA;
    private final Location beaconTeamB;
    private final Location lobbySpawn;

    public Arena(String name, Location spawnTeamA, Location spawnTeamB,
                 Location beaconTeamA, Location beaconTeamB, Location lobbySpawn) {
        this.name = name;
        this.spawnTeamA = spawnTeamA;
        this.spawnTeamB = spawnTeamB;
        this.beaconTeamA = beaconTeamA;
        this.beaconTeamB = beaconTeamB;
        this.lobbySpawn = lobbySpawn;
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

    public static Arena createDefault() {
        World world = Bukkit.getWorld("flat");
        return new Arena(
                "arena1",
                new Location(world, 19, -60, 19),   // spawn équipe A
                new Location(world, 19, -60, 63),  // spawn équipe B
                new Location(world, 19, -59, 8),    // beacon équipe A
                new Location(world, 19, -59, 75),   // beacon équipe B
                new Location(world, -2, -60, 63)      // spawn lobby
        );
    }
}
