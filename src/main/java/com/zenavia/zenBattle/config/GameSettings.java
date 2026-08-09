package com.zenavia.zenBattle.config;

public record GameSettings(
        int minPlayersToStart,
        int countdownSeconds,
        int endDelaySeconds,
        int beaconMaxHealth,
        int damagePerHit,
        String teamAName,
        String teamBName
) {
}
