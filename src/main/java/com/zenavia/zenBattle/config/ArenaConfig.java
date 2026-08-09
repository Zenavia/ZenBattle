package com.zenavia.zenBattle.config;

import org.bukkit.Location;
import org.bukkit.Material;

public record ArenaConfig(
        String name,
        Location spawnTeamA,
        Location spawnTeamB,
        Location beaconTeamA,
        Location beaconTeamB,
        Location lobbySpawn,
        Location barrierCorner1,
        Location barrierCorner2,
        Material barrierMaterial
) { }
