package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.config.BeaconHealthBarManager;
import com.zenavia.zenBattle.config.GameSettings;
import com.zenavia.zenBattle.config.MessageManager;
import com.zenavia.zenBattle.config.TitleManager;
import com.zenavia.zenBattle.util.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GameManager {
    private final Plugin plugin;
    private final ArenaManager arenaManager;
    private final Game game;
    private final GameSettings settings;
    private final MessageManager messageManager;
    private final TitleManager titleManager;
    private final BeaconHealthBarManager beaconHealthBarManager;
    private Countdown countdown;

    public GameManager(Plugin plugin, ArenaManager arenaManager, GameSettings settings, MessageManager messageManager, TitleManager titleManager, BeaconHealthBarManager beaconHealthBarManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;

        this.settings = settings;

        this.game = new Game(new Team(settings.teamAName(), settings), new Team(settings.teamBName(), settings));

        this.messageManager = messageManager;
        this.titleManager = titleManager;
        this.beaconHealthBarManager = beaconHealthBarManager;
    }

    public Game getGame() {
        return game;
    }

    public void addPlayerToGame(Player player) {
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            player.sendMessage(messageManager.get("game.no-arena"));
            return;
        }
        Arena arena = arenaOpt.get();

        if (game.getState() != GameState.WAITING) {
            player.sendMessage(messageManager.get("game.game-already-started"));
            return;
        }

        UUID uuid = player.getUniqueId();
        Team teamA = game.getTeamA();
        Team teamB = game.getTeamB();

        Team target = teamA.getPlayers().size() <= teamB.getPlayers().size() ? teamA : teamB;
        target.addPlayer(uuid);

        player.teleport(target == teamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB());
        player.sendMessage(messageManager.get("team.joined", Map.of("team", target.getName())));
        if (allPlayers().size() < settings.minPlayersToStart()) {
            Bukkit.broadcast(messageManager.get("game.not-enough-players", Map.of("min-players", String.valueOf(settings.minPlayersToStart()), "max-players", "2")));
        }

        checkStartConditions();
    }

    private void checkStartConditions() {
        if (game.getState() == GameState.WAITING && game.totalPlayers() >= settings.minPlayersToStart()) {
            startCountdown();
        }
    }

    private void startCountdown() {
        game.setState(GameState.STARTING);

        countdown = new Countdown(plugin, settings.countdownSeconds(),
                () -> {
                    Bukkit.broadcast(messageManager.get("game.countdown-start", Map.of("seconds", String.valueOf(countdown.getSecondsLeft()))));
                },
                this::startGame
        );
        countdown.start();
    }

    private void startGame() {
        game.setState(GameState.PLAYING);
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            plugin.getLogger().severe("Impossible de démarrer la partie : aucune arène disponible.");
            game.setState(GameState.WAITING);
            return;
        }
        Arena arena = arenaOpt.get();
        game.getTeamA().setBeaconLocation(arena.getBeaconTeamA());
        game.getTeamB().setBeaconLocation(arena.getBeaconTeamB());
        Bukkit.broadcast(messageManager.get("game.starting"));
        titleManager.showToAll(Bukkit.getOnlinePlayers(),
                messageManager.get("game.starting-title"),
                messageManager.get("game.starting-subtitle"));
    }

    public void onGameEnding() {
        Countdown endCountdown = new Countdown(plugin, settings.endDelaySeconds(),
                () -> {
                },
                this::resetGame
        );
        endCountdown.start();
    }

    public void resetGame() {
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            plugin.getLogger().severe("Impossible de reset la partie : aucune arène disponible.");
            game.setState(GameState.WAITING);
            return;
        }
        Arena arena = arenaOpt.get();

        for (UUID uuid : allPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.teleport(arena.getLobbySpawn());
            }
        }

        game.getTeamA().reset();
        game.getTeamB().reset();
        game.setState(GameState.WAITING);
        beaconHealthBarManager.clearAll(Bukkit.getOnlinePlayers());
        Bukkit.broadcast(messageManager.get("game.back-lobby"));
    }

    private Set<UUID> allPlayers() {
        Set<UUID> all = new HashSet<>(game.getTeamA().getPlayers());
        all.addAll(game.getTeamB().getPlayers());
        return all;
    }
}
