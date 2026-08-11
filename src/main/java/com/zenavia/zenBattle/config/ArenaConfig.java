package com.zenavia.zenBattle.config;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public record ArenaConfig(
        String name,
        Location spawnTeamA,
        Location spawnTeamB,
        Location beaconTeamA,
        Location beaconTeamB,
        Location lobbySpawn,
        Location barrierCorner1,
        Location barrierCorner2,
        Material barrierMaterial,
        List<Location> spawnPointsTeamA,
        List<Location> spawnPointsTeamB
) { }
