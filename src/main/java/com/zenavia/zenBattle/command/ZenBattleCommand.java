package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public class ZenBattleCommand implements CommandExecutor {
    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public ZenBattleCommand(GameManager gameManager, ArenaManager arenaManager) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
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
            Arena arena = arenaManager.getOrCreateArena();
            gameManager.addPlayerToGame(player);
            return true;
        }

        if(args[0].equalsIgnoreCase("list")) {
            gameManager.getGame().getTeamA().getPlayers().forEach(uuid -> player.sendMessage("Team A: " + UUID.fromString(uuid.toString())));
            gameManager.getGame().getTeamB().getPlayers().forEach(uuid -> player.sendMessage("Team B: " + UUID.fromString(uuid.toString())));
            Logger.getLogger("ZenBattle").info("Team A: " + gameManager.getGame().getTeamA().getPlayers().toString());
            return true;
        }

        return true;
    }
}
