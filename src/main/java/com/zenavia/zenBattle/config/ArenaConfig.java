package com.zenavia.zenBattle.config;

import org.bukkit.Location;

public record ArenaConfig(
        String name,
        Location spawnTeamA,
        Location spawnTeamB,
        Location beaconTeamA,
        Location beaconTeamB,
        Location lobbySpawn

) { }
