package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ZenBattleCommand implements CommandExecutor {
    private final GameManager gameManager;

    public ZenBattleCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /zb join");
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            gameManager.addPlayerToGame(player);
            return true;
        }

        return true;
    }
}
