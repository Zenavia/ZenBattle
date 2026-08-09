package com.zenavia.zenBattle.game;

import com.zenavia.zenBattle.arena.Arena;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameManager {
    // gestion du cycle de vie / plusieurs arènes
    private final Arena arena;
    private final Game game;

    public GameManager(Arena arena) {
        this.arena = arena;
        this.game = new Game(new Team("A"), new Team("B"));
    }

    public Game getGame() {
        return game;
    }

    public Arena getArena() {
        return arena;
    }

    public void addPlayerToGame(Player player) {
        UUID uuid = player.getUniqueId();
        Team teamA = game.getTeamA();
        Team teamB = game.getTeamB();

        Team target = teamA.getPlayers().size() <= teamB.getPlayers().size() ? teamA : teamB;
        target.addPlayer(uuid);

        player.teleport(target == teamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB());
        player.sendMessage("Tu as rejoint l'équipe " + target.getName());
    }
}
