package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.config.*;
import com.zenavia.zenBattle.game.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;

public class BeaconDamageListener implements Listener {

    private final GameManager gameManager;
    private final GameFeedback feedback;
    private final ConfigManager configManager;

    public BeaconDamageListener(GameManager gameManager, GameFeedback feedback, ConfigManager configManager) {
        this.gameManager = gameManager;
        this.feedback = feedback;
        this.configManager = configManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.BEACON) return;

        Game game = gameManager.getGame();
        if (game.getState() != GameState.PLAYING) return;

        Location clicked = event.getClickedBlock().getLocation();
        Team target = matchTeamByBeacon(game, clicked);
        if (target == null) return;

        Player player = event.getPlayer();
        Team playerTeam = game.getTeamOfPlayer(player.getUniqueId());
        if (playerTeam == null) return; // spectateur/hors partie
        if (playerTeam == target) {
            feedback.ownBeaconDenied(player);
            return;
        }

        boolean destroyed = target.damageBeacon(configManager.getSettings().damagePerHit());
        feedback.beaconHit(target, clicked);

        if (destroyed) {
            game.onBeaconDestroyed(target);
            Team winner = game.getOtherTeam(target);
            feedback.victory(winner, target.getBeaconLocation());

            gameManager.onGameEnding();
        }
    }

    private Team matchTeamByBeacon(Game game, Location clicked) {
        if (sameBlock(game.getTeamA().getBeaconLocation(), clicked)) return game.getTeamA();
        if (sameBlock(game.getTeamB().getBeaconLocation(), clicked)) return game.getTeamB();
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
