package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.config.*;
import com.zenavia.zenBattle.kits.Kit;
import com.zenavia.zenBattle.kits.KitManager;
import com.zenavia.zenBattle.kits.KitMenu;
import com.zenavia.zenBattle.util.Countdown;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class GameManager {
    private final Plugin plugin;
    private final ArenaManager arenaManager;
    private final ConfigManager configManager;
    private final GameFeedback feedback;
    private final Game game;
    private final KitManager kitManager;
    private final KitMenu kitMenu;
    private final BarrierManager barrierManager;
    private Countdown countdown;

    public GameManager(Plugin plugin, ArenaManager arenaManager, ConfigManager configManager, GameFeedback feedback, KitManager kitManager, KitMenu kitMenu, BarrierManager barrierManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.configManager = configManager;
        this.feedback = feedback;
        this.game = new Game(new Team(configManager.getSettings().teamAName(), configManager), new Team(configManager.getSettings().teamBName(), configManager));
        this.kitManager = kitManager;
        this.kitMenu = kitMenu;
        this.barrierManager = barrierManager;
    }

    public Game getGame() {
        return game;
    }

    public void addPlayerToGame(Player player) {
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            feedback.noArena(player);
            return;
        }
        Arena arena = arenaOpt.get();

        if (game.getState() != GameState.WAITING) {
            feedback.alreadyStarted(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        Team teamA = game.getTeamA();
        Team teamB = game.getTeamB();

        Team target = teamA.getPlayers().size() <= teamB.getPlayers().size() ? teamA : teamB;
        target.addPlayer(uuid);
        kitMenu.open(player);

        player.teleport(target == teamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB());
        feedback.playerJoined(player, target);
        if (allPlayers().size() < configManager.getSettings().minPlayersToStart()) {
            feedback.notEnoughPlayer(game);
        }

        checkStartConditions();
    }

    private void checkStartConditions() {
        if (game.getState() == GameState.WAITING && game.totalPlayers() >= configManager.getSettings().minPlayersToStart()) {
            startCountdown();
        }
    }

    private void startCountdown() {
        game.setState(GameState.STARTING);

        countdown = new Countdown(plugin, configManager.getSettings().countdownSeconds(),
                () -> {
                    feedback.countdownStarted(countdown.getSecondsLeft());
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

        teleportTeamToSpawn(game.getTeamA(), arena.getSpawnPointsTeamA(), arena.getSpawnTeamA());
        teleportTeamToSpawn(game.getTeamB(), arena.getSpawnPointsTeamB(), arena.getSpawnTeamB());
        setGamemodeForAllPlayers(game.getBothTeams());

        game.getTeamA().setBeaconLocation(arena.getBeaconTeamA());
        game.getTeamB().setBeaconLocation(arena.getBeaconTeamB());
        feedback.gameStarted();
        breakBarrier(arena);
    }

    private void teleportTeamToSpawn(Team team, List<Location> spawnPoints, Location fallback) {
        List<UUID> players = new ArrayList<>(team.getPlayers());

        for (int i = 0; i < players.size(); i++) {
            Player player = Bukkit.getPlayer(players.get(i));
            if (player == null) continue;

            Location spawn;
            if (spawnPoints.isEmpty()) {
                spawn = fallback; // sécurité si aucun marqueur trouvé sur la map
            } else {
                spawn = spawnPoints.get(i % spawnPoints.size()); // round-robin
            }

            player.teleport(spawn);
            applyKit(player);
        }
    }

    private void setGamemodeForAllPlayers(Set<UUID> team) {
        for (UUID uuid : team) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
    }

    private void applyKit(Player player) {
        Kit kit = kitManager.getKit(player.getUniqueId());
        player.getInventory().clear();
        player.getInventory().addItem(kit.buildItems());
    }

    public void onGameEnding() {
        Countdown endCountdown = new Countdown(plugin, configManager.getSettings().endDelaySeconds(),
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
                kitManager.clear(uuid);
                player.getInventory().clear();
            }
        }
        rebuildBarrier(arena);

        game.getTeamA().reset();
        game.getTeamB().reset();
        game.setState(GameState.WAITING);
        kitManager.clearAll();
        feedback.gameReset();
    }

    private Set<UUID> allPlayers() {
        Set<UUID> all = new HashSet<>(game.getTeamA().getPlayers());
        all.addAll(game.getTeamB().getPlayers());
        return all;
    }

    private void breakBarrier(Arena arena) {
        barrierManager.breakBarrier(arena.getBarrierCorner1(), arena.getBarrierCorner2(), arena.getBarrierMaterial());
    }

    private void rebuildBarrier(Arena arena) {
        barrierManager.rebuildBarrier(arena.getBarrierCorner1(), arena.getBarrierCorner2(), arena.getBarrierMaterial());
    }

    public void stopGame() {
        this.resetGame();
        feedback.gameStoped();
    }
}
