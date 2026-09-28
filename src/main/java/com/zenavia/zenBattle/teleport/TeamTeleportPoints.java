package com.zenavia.zenBattle.teleport;

import org.bukkit.Location;

import java.util.List;

public record TeamTeleportPoints(
        List<Location> near,
        List<Location> mid,
        List<Location> far
) {
    public List<Location> forTier(TeleportTier tier) {
        return switch (tier) {
            case NEAR -> near;
            case MID -> mid;
            case FAR -> far;
        };
    }
}
