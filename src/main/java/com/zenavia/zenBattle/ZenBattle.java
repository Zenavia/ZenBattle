package com.zenavia.zenBattle;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.command.ZenBattleCommand;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.listener.BeaconBreakListener;
import com.zenavia.zenBattle.listener.BeaconDamageListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZenBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin démarré.");
        getLogger().info("Liste des mondes : " + Bukkit.getWorlds());
        ArenaManager arenaManager = new ArenaManager();
        GameManager gameManager = new GameManager(this, arenaManager);
        getServer().getPluginManager().registerEvents(new BeaconBreakListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new BeaconDamageListener(gameManager), this);
        Objects.requireNonNull(getCommand("zb")).setExecutor(new ZenBattleCommand(gameManager, arenaManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin éteint.");
    }
}
