package com.zenavia.zenBattle.arena;

import com.zenavia.zenBattle.config.ArenaConfig;
import com.zenavia.zenBattle.config.ConfigManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ArenaManager {
    private final ConfigManager configManager;
    private Arena arena;
    private final Logger logger;

    public ArenaManager(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
    }

    public Optional<Arena> getOrCreateArena() {
        if(arena != null) return Optional.of(arena);

        List<ArenaConfig> arenas = configManager.loadArenas();
        if (arenas.isEmpty()) {
            logger.warning("Aucune arène configurée dans plugins/ZenBattle/arenas/ - le jeu est indisponible tant qu'aucune arène n'est ajoutée.");
            return Optional.empty();
        }

        ArenaConfig cfg = arenas.getFirst();
        arena = new Arena(cfg.name(), cfg.spawnTeamA(), cfg.spawnTeamB(),
                cfg.beaconTeamA(), cfg.beaconTeamB(), cfg.lobbySpawn());

        return Optional.of(arena);
    }
}
