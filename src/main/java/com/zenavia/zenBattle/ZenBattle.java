package com.zenavia.zenBattle;

import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.command.AdminCommand;
import com.zenavia.zenBattle.command.ReloadCommand;
import com.zenavia.zenBattle.command.ZenBattleCommand;
import com.zenavia.zenBattle.config.*;
import com.zenavia.zenBattle.config.ConfigManager;
import com.zenavia.zenBattle.game.BarrierManager;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.kits.KitManager;
import com.zenavia.zenBattle.kits.KitMenu;
import com.zenavia.zenBattle.listener.BeaconDamageListener;
import com.zenavia.zenBattle.listener.FriendlyFireListener;
import com.zenavia.zenBattle.listener.KitMenuListener;
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
        ActionBarManager actionBarManager = new ActionBarManager(messageManager);
        GameFeedback feedback = new GameFeedback(messageManager, titleManager, healthBarManager, configManager, actionBarManager);
        BarrierManager barrierManager = new BarrierManager(this);

        KitMenu kitMenu = new KitMenu();
        KitManager kitManager = new KitManager();

        ArenaManager arenaManager = new ArenaManager(configManager, getLogger());
        GameManager gameManager = new GameManager(this, arenaManager, configManager, feedback, kitManager, kitMenu, barrierManager);
        AdminCommand adminCommand = new AdminCommand(gameManager, configManager);

        getServer().getPluginManager().registerEvents(new BeaconDamageListener(gameManager, feedback, configManager), this);
        getServer().getPluginManager().registerEvents(new FriendlyFireListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new KitMenuListener(kitMenu, kitManager), this);

        Objects.requireNonNull(getCommand("zb")).setExecutor(new ZenBattleCommand(gameManager, kitMenu, adminCommand));
        Objects.requireNonNull(getCommand("zbreload")).setExecutor(new ReloadCommand(this, configManager, messageManager, gameManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin éteint.");
    }
}
