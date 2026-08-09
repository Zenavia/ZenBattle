package com.zenavia.zenBattle.game;

public record GameSettings(
        int minPlayersToStart,
        int countdownSeconds,
        int endDelaySeconds,
        int beaconMaxHealth,
        int damagePerHit
) {}
