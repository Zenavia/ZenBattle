package com.zenavia.zenBattle;

import com.zenavia.zenBattle.arena.Arena;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZenBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[ZENBATTLE] Plugin démarré.");
        Arena testArene = Arena.createDefault();
        getLogger().info("[ZENBATTLE] Arena créée: " + testArene.getSpawnTeamA());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[ZENBATTLE] Plugin éteint.");
    }
}
