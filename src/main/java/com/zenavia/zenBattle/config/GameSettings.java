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
    public GameSettings withMinPlayersToStart(int value) {
        return new GameSettings(value, countdownSeconds, endDelaySeconds, beaconMaxHealth, damagePerHit, teamAName, teamBName);
    }

    public GameSettings withBeaconMaxHealth(int value) {
        return new GameSettings(minPlayersToStart, countdownSeconds, endDelaySeconds, value, damagePerHit, teamAName, teamBName);
    }

    public GameSettings withDamagePerHit(int value) {
        return new GameSettings(minPlayersToStart, countdownSeconds, endDelaySeconds, beaconMaxHealth, value, teamAName, teamBName);
    }

    public GameSettings withCountdownSeconds(int value) {
        return new GameSettings(minPlayersToStart, value, endDelaySeconds, beaconMaxHealth, damagePerHit, teamAName, teamBName);
    }

    public GameSettings withEndDelaySeconds(int value) {
        return new GameSettings(minPlayersToStart, countdownSeconds, value, beaconMaxHealth, damagePerHit, teamAName, teamBName);
    }
}
