package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.config.*;
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
    private Countdown countdown;

    public GameManager(Plugin plugin, ArenaManager arenaManager, ConfigManager configManager, GameFeedback feedback) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.configManager = configManager;
        this.feedback = feedback;
        this.game = new Game(new Team(configManager.getSettings().teamAName(), configManager), new Team(configManager.getSettings().teamBName(), configManager));
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
        game.getTeamA().setBeaconLocation(arena.getBeaconTeamA());
        game.getTeamB().setBeaconLocation(arena.getBeaconTeamB());
        feedback.gameStarted();
        breakBarrier(arena);
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
            }
        }
        rebuildBarrier(arena);

        game.getTeamA().reset();
        game.getTeamB().reset();
        game.setState(GameState.WAITING);
        feedback.gameReset();
    }

    private Set<UUID> allPlayers() {
        Set<UUID> all = new HashSet<>(game.getTeamA().getPlayers());
        all.addAll(game.getTeamB().getPlayers());
        return all;
    }

    private void breakBarrier(Arena arena) {
        Location corner1 = arena.getBarrierCorner1();
        Location corner2 = arena.getBarrierCorner2();
        Material material = arena.getBarrierMaterial();
        if (corner1 == null || corner2 == null || material == null) return; // pas configuré, on ignore

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        World world = corner1.getWorld();

        int brokenCount = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != material) continue; // ne touche que le bon type
                    block.setType(Material.AIR);
                    brokenCount++;
                }
            }
        }

        if (brokenCount == 0) return; // rien trouvé, pas de son/particule inutile

        Location center = corner1.clone().add(corner2).multiply(0.5);
        world.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.7f);
        world.spawnParticle(Particle.EXPLOSION, center, 5, 1, 1, 1, 0.1);
    }

    private void rebuildBarrier(Arena arena) {
        Location corner1 = arena.getBarrierCorner1();
        Location corner2 = arena.getBarrierCorner2();
        Material material = arena.getBarrierMaterial();
        if (corner1 == null || corner2 == null || material == null) return;

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        World world = corner1.getWorld();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }
}
