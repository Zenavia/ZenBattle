package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.kits.KitMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public class ZenBattleCommand implements CommandExecutor {
    private final GameManager gameManager;
    private final KitMenu kitMenu;

    public ZenBattleCommand(GameManager gameManager, KitMenu kitMenu) {
        this.gameManager = gameManager;
        this.kitMenu = kitMenu;
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

        if(args[0].equalsIgnoreCase("list")) {
            gameManager.getGame().getTeamA().getPlayers().forEach(uuid -> player.sendMessage("Team A: " + UUID.fromString(uuid.toString())));
            gameManager.getGame().getTeamB().getPlayers().forEach(uuid -> player.sendMessage("Team B: " + UUID.fromString(uuid.toString())));
            Logger.getLogger("ZenBattle").info("Team A: " + gameManager.getGame().getTeamA().getPlayers().toString());
            return true;
        }

        if (args[0].equalsIgnoreCase("kit")) {
            if (gameManager.getGame().getState().equals(GameState.WAITING)) {
                kitMenu.open(player);
            }else if(gameManager.getGame().getTeamOfPlayer(player.getUniqueId()) == null) {
                player.sendMessage(Component.text("Aucune partie en cours.", NamedTextColor.RED));
            }
            return true;
        }

        return true;
    }
}
