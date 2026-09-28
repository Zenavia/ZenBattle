package com.zenavia.zenBattle.teleport;

import com.zenavia.zenBattle.config.ConfigManager;
import com.zenavia.zenBattle.game.Team;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class TeleportPointManager {
    private final ConfigManager configManager;
    private final Random random = new Random();

    public TeleportPointManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * @param target l'équipe dont le beacon vient d'être touché
     * @param attackerPoints les points de téléportation de l'équipe attaquante (adverse à target)
     */
    public Location resolveTeleportPoint(Team target, TeamTeleportPoints attackerPoints) {
        TeleportTier tier = resolveTier(target);
        List<Location> candidates = attackerPoints.forTier(tier);

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private TeleportTier resolveTier(Team target) {
        int maxHealth = configManager.getSettings().beaconMaxHealth();
        float ratio = (float) target.getBeaconHealth() / maxHealth;

        if (ratio > 0.66f) return TeleportTier.NEAR;
        if (ratio > 0.33f) return TeleportTier.MID;
        return TeleportTier.FAR;
    }
}