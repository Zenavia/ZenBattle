package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.util.Countdown;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class GameManager {
    // gestion du cycle de vie / plusieurs arènes
    private static final int MIN_PLAYERS_TO_START = 1;
    private static final int COUNTDOWN_SECONDS = 10;

    private final Plugin plugin;
    private final ArenaManager arenaManager;
    private final Game game;
    private Countdown countdown;

    public GameManager(Plugin plugin, ArenaManager arenaManager) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.game = new Game(new Team("A"), new Team("B"));
    }

    public Game getGame() {
        return game;
    }

    public void addPlayerToGame(Player player) {
        Arena arena = arenaManager.getOrCreateArena();

        UUID uuid = player.getUniqueId();
        Team teamA = game.getTeamA();
        Team teamB = game.getTeamB();

        Team target = teamA.getPlayers().size() <= teamB.getPlayers().size() ? teamA : teamB;
        target.addPlayer(uuid);

        player.teleport(target == teamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB());
        player.sendMessage("Tu as rejoint l'équipe " + target.getName());

        checkStartConditions();
    }

    private void checkStartConditions(){
        if(game.getState() == GameState.WAITING && game.totalPlayers() >= MIN_PLAYERS_TO_START){
            startCountdown();
        }
    }

    private void startCountdown(){
        game.setState(GameState.STARTING);
        Bukkit.broadcast(Component.text("La partie va commencer dans " + COUNTDOWN_SECONDS + " secondes !"));

        countdown = new Countdown(plugin, COUNTDOWN_SECONDS,
                () -> {
                    Bukkit.broadcast(Component.text("La partie commence dans " + countdown.getSecondsLeft() + " secondes !"));
                },
                this::startGame
        );
        countdown.start();
    }

    private void startGame(){
        game.setState(GameState.PLAYING);
        Arena arena = arenaManager.getOrCreateArena();
        game.getTeamA().setBeaconLocation(arena.getBeaconTeamA());
        game.getTeamB().setBeaconLocation(arena.getBeaconTeamB());
        Bukkit.broadcast(Component.text("La partie commence ! Bonne chance !"));
    }
}
