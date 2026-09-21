package com.zenavia.zenBattle.command;

import com.zenavia.zenBattle.config.ConfigManager;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class AdminCommand {
    private final GameManager gameManager;
    private final ConfigManager configManager;

    public AdminCommand(GameManager gameManager, ConfigManager configManager) {
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    public void handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zenbattle.admin")) {
            sender.sendMessage("§cPermission refusée.");
            return;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "setminplayers" -> setMinPlayers(sender, args);
            case "setbeaconhealth" -> setBeaconHealth(sender, args);
            case "forcestart" -> forceStart(sender);
            case "forceend" -> forceEnd(sender, args);
            case "restart" -> restart(sender);
            case "setdamage" -> setDamagePerHit(sender, args);
            default -> sendUsage(sender);
        }
    }

    private void setMinPlayers(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /zb admin setminplayers <nombre>");
            return;
        }
        int value = parseIntOrError(sender, args[2]);
        if (value < 0) return;

        configManager.setMinPlayersToStart(value);
        sender.sendMessage("§aMin joueurs pour démarrer : §e" + value);
    }

    private void setBeaconHealth(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /zb admin setbeaconhealth <valeur>");
            return;
        }
        int value = parseIntOrError(sender, args[2]);
        if (value <= 0) return;

        if (gameManager.getGame().getState() == GameState.PLAYING) {
            sender.sendMessage("§cImpossible de changer la vie du beacon pendant une partie en cours.");
            return;
        }

        configManager.setBeaconMaxHealth(value);
        sender.sendMessage("§aVie max du beacon : §e" + value);
    }

    private void setDamagePerHit(CommandSender sender, String[] args){
        if(args.length < 3){
            sender.sendMessage("§cUsage: /zb admin setdamage <valeur>");
            return;
        }
        int value = parseIntOrError(sender, args[2]);
        if(value <= 0) return;

        if(gameManager.getGame().getState() == GameState.PLAYING) {
            sender.sendMessage("§cImpossible de changer les dégâts par frappe pendant une partie en cours.");
            return;
        }
        configManager.setDamagePerHit(value);
        sender.sendMessage("§aDégâts par frappe : §e" + value);
    }

    private void forceStart(CommandSender sender) {
        if (gameManager.getGame().getState() != GameState.WAITING) {
            sender.sendMessage("§cLa partie n'est pas en attente.");
            return;
        }
        gameManager.forceStart();
        sender.sendMessage("§aDémarrage forcé de la partie.");
    }

    private void forceEnd(CommandSender sender, String[] args) {
        if (gameManager.getGame().getState() != GameState.PLAYING) {
            sender.sendMessage("§cAucune partie en cours.");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /zb admin forceEnd <équipe>");
            return;
        }
        if(!Objects.equals(args[2], gameManager.getGame().getTeamA().getName()) && !Objects.equals(args[2], gameManager.getGame().getTeamB().getName())) {
            sender.sendMessage("§cÉquipe invalide.");
            return;
        }
        gameManager.forceEnd(args[2]);
        sender.sendMessage("§aPartie terminée de force.");
    }

    private void restart(CommandSender sender) {
        gameManager.resetGame();
        sender.sendMessage("§aPartie redémarrée.");
    }

    private int parseIntOrError(CommandSender sender, String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValeur invalide : " + raw);
            return -1;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("""
            §cUsage:
            §7/zb admin setMinPlayers <nombre>
            §7/zb admin setBeaconHealth <valeur>
            §7/zb admin forceStart
            §7/zb admin forceEnd
            §7/zb admin restart""");
    }
}
