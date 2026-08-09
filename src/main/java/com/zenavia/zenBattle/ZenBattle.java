package com.zenavia.zenBattle;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.command.ZenBattleCommand;
import com.zenavia.zenBattle.game.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZenBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[ZENBATTLE] Plugin démarré.");
        Arena testArene = Arena.createDefault();
        GameManager gameManager = new GameManager(testArene);
        Objects.requireNonNull(getCommand("zb")).setExecutor(new ZenBattleCommand(gameManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[ZENBATTLE] Plugin éteint.");
    }
}
