package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.config.ConfigManager;
import com.zenavia.zenBattle.config.MessageManager;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final GameManager gameManager;

    public ReloadCommand(Plugin plugin, ConfigManager configManager,
                         MessageManager messageManager, GameManager gameManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zenbattle.reload")) {
            sender.sendMessage("§cPermission refusée.");
            return true;
        }

        if (gameManager.getGame().getState() == GameState.PLAYING) {
            sender.sendMessage("§cImpossible de reload pendant une partie en cours.");
            return true;
        }

        configManager.reload();
        messageManager.reload(plugin);
        sender.sendMessage("§aConfiguration et messages rechargés.");
        return true;
    }
}
