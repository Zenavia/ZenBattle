package com.zenavia.zenBattle;

import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.command.ZenBattleCommand;
import com.zenavia.zenBattle.config.*;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.listener.BeaconBreakListener;
import com.zenavia.zenBattle.listener.BeaconDamageListener;
import com.zenavia.zenBattle.listener.FriendlyFireListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ZenBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin démarré.");
        getLogger().info("Liste des mondes : " + Bukkit.getWorlds());

        ConfigManager configManager = new ConfigManager(this);
        configManager.loadAll();

        MessageManager messageManager = new MessageManager(this);
        TitleManager titleManager = new TitleManager();
        BeaconHealthBarManager healthBarManager = new BeaconHealthBarManager();
        GameFeedback feedback = new GameFeedback(messageManager, titleManager, healthBarManager, configManager.getSettings());

        ArenaManager arenaManager = new ArenaManager(configManager, getLogger());
        GameManager gameManager = new GameManager(this, arenaManager, configManager.getSettings(), feedback);

        getServer().getPluginManager().registerEvents(new BeaconDamageListener(gameManager, feedback, configManager.getSettings()), this);
        getServer().getPluginManager().registerEvents(new FriendlyFireListener(gameManager), this);

        Objects.requireNonNull(getCommand("zb")).setExecutor(new ZenBattleCommand(gameManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin éteint.");
    }
}
