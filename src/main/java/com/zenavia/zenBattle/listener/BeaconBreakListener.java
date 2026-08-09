package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.game.Game;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.game.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BeaconBreakListener implements Listener {
    private final GameManager gameManager;

    public BeaconBreakListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.BEACON) return;

        Game game = gameManager.getGame();
        if (game.getState() != GameState.PLAYING) return;

        Location broken = event.getBlock().getLocation();
        Team target = matchTeamByBeacon(game, broken);
        if (target == null) return;

        game.onBeaconDestroyed(target);

        Team winner = game.getOtherTeam(target);
        Bukkit.broadcast(Component.text("L'équipe " + winner.getName() + " a gagné en détruisant le beacon adverse !"));
    }

    private Team matchTeamByBeacon(Game game, Location broken) {
        if (sameBlock(game.getTeamA().getBeaconLocation(), broken)) return game.getTeamA();
        if (sameBlock(game.getTeamB().getBeaconLocation(), broken)) return game.getTeamB();
        return null;
    }

    private boolean sameBlock(Location a, Location b) {
        if (a == null || b == null) return false;
        return a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
