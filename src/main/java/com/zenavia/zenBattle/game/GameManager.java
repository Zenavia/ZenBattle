package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.util.Countdown;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GameManager {
    private final Plugin plugin;
    private final ArenaManager arenaManager;
    private final Game game;
    private final GameSettings settings;
    private Countdown countdown;

    public GameManager(Plugin plugin, ArenaManager arenaManager, GameSettings settings) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.settings = settings;
        this.game = new Game(new Team("A", settings), new Team("B", settings));
    }

    public Game getGame() {
        return game;
    }

    public void addPlayerToGame(Player player) {
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            player.sendMessage("§cAucune arène n'est configurée pour le moment, contactez un Administrateur.");
            return;
        }
        Arena arena = arenaOpt.get();

        if (game.getState() != GameState.WAITING) {
            player.sendMessage("La partie a déjà commencé.");
            return;
        }

        UUID uuid = player.getUniqueId();
        Team teamA = game.getTeamA();
        Team teamB = game.getTeamB();

        Team target = teamA.getPlayers().size() <= teamB.getPlayers().size() ? teamA : teamB;
        target.addPlayer(uuid);

        player.teleport(target == teamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB());
        player.sendMessage("Tu as rejoint l'équipe " + target.getName());
        if(allPlayers().size() < settings.minPlayersToStart()){
            Bukkit.broadcast(Component.text(allPlayers().size() + "/" + settings.minPlayersToStart() + " joueurs dans ZenBattle"));
        }

        checkStartConditions();
    }

    private void checkStartConditions(){
        if(game.getState() == GameState.WAITING && game.totalPlayers() >= settings.minPlayersToStart()){
            startCountdown();
        }
    }

    private void startCountdown(){
        game.setState(GameState.STARTING);
        Bukkit.broadcast(Component.text("La partie va commencer dans " + settings.countdownSeconds() + " secondes !"));

        countdown = new Countdown(plugin, settings.countdownSeconds(),
                () -> {
                    Bukkit.broadcast(Component.text("La partie commence dans " + countdown.getSecondsLeft() + " secondes !"));
                },
                this::startGame
        );
        countdown.start();
    }

    private void startGame(){
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
        Bukkit.broadcast(Component.text("La partie commence ! Bonne chance !"));
    }

    public void onGameEnding(){
        Countdown endCountdown = new Countdown(plugin, settings.endDelaySeconds(),
                () -> {},
                this::resetGame
        );
        endCountdown.start();
    }

    public void resetGame(){
        Optional<Arena> arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) {
            plugin.getLogger().severe("Impossible de reset la partie : aucune arène disponible.");
            game.setState(GameState.WAITING);
            return;
        }
        Arena arena = arenaOpt.get();

        for(UUID uuid : allPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                player.teleport(arena.getLobbySpawn());
            }
        }

        game.getTeamA().reset();
        game.getTeamB().reset();
        game.setState(GameState.WAITING);
        Bukkit.broadcast(Component.text("La partie est terminée. Retour au lobby."));
    }

    private Set<UUID> allPlayers() {
        Set<UUID> all = new HashSet<>(game.getTeamA().getPlayers());
        all.addAll(game.getTeamB().getPlayers());
        return all;
    }
}
