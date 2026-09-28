package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.arena.ArenaManager;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.game.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerDeathListener implements Listener {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;

    public PlayerDeathListener(GameManager gameManager, ArenaManager arenaManager) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (gameManager.getGame().getState() != GameState.PLAYING) return;

        Player player = event.getPlayer();
        Team playerInTeam = gameManager.getGame().getTeamOfPlayer(player.getUniqueId());
        if (playerInTeam == null) return;

        var arenaOpt = arenaManager.getOrCreateArena();
        if (arenaOpt.isEmpty()) return;

        Arena arena = arenaOpt.get();
        boolean isTeamA = playerInTeam == gameManager.getGame().getTeamA();

        List<Location> spawnPoints = isTeamA ? arena.getSpawnPointsTeamA() : arena.getSpawnPointsTeamB();
        Location fallback = isTeamA ? arena.getSpawnTeamA() : arena.getSpawnTeamB();

        Location spawn = (!spawnPoints.isEmpty())
                ? spawnPoints.get(ThreadLocalRandom.current().nextInt(spawnPoints.size()))
                : fallback;

        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }
}