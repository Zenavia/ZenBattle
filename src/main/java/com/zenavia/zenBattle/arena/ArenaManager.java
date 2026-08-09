package com.zenavia.zenBattle.arena;

public class ArenaManager {
    // charge et valide les arènes dispos
    private Arena arena;

    public Arena getOrCreateArena() {
        if (arena == null) {
            arena = Arena.createDefault();
        }
        return arena;
    }
}
