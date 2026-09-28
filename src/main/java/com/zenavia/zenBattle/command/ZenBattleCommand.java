package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.kits.KitMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZenBattleCommand implements TabExecutor {
    private final GameManager gameManager;
    private final KitMenu kitMenu;
    private final AdminCommand adminCommand;

    public ZenBattleCommand(GameManager gameManager, KitMenu kitMenu, AdminCommand adminCommand) {
        this.gameManager = gameManager;
        this.kitMenu = kitMenu;
        this.adminCommand = adminCommand;
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

        if(args[0].equalsIgnoreCase("admin")){
            adminCommand.handle(player, args);
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("join", "admin", "kit");
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return List.of("setMinPlayers", "setBeaconHealth", "forceStart", "forceEnd", "restart");
        }
        if(args.length == 3 && args[2].equalsIgnoreCase("setMinPlayers")) {
            return List.of("5");
        }
        if(args.length == 3 && args[2].equalsIgnoreCase("setBeaconHealth")) {
            return List.of("200");
        }
        return List.of();
    }
}
