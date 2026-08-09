package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.game.Game;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.game.Team;
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

import java.util.UUID;

public class BeaconDamageListener implements Listener {

    private static final int DAMAGE_PER_HIT = 1;

    private final GameManager gameManager;

    public BeaconDamageListener(GameManager gameManager) {
        this.gameManager = gameManager;
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
            player.sendMessage("Tu ne peux pas attaquer ton propre beacon !");
            return;
        }

        boolean destroyed = target.damageBeacon(DAMAGE_PER_HIT);
        player.playSound(clicked, Sound.BLOCK_ANVIL_LAND, 1f, 1.5f); // feedback provisoire, à styliser plus tard
        Bukkit.broadcast(Component.text("Beacon " + target.getName() + " : " + target.getBeaconHealth() + " PV restants"));

        if (destroyed) {
            game.onBeaconDestroyed(target);
            Team winner = game.getOtherTeam(target);
            Bukkit.broadcast(Component.text("L'équipe " + winner.getName() + " a gagné en détruisant le beacon adverse !"));
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
