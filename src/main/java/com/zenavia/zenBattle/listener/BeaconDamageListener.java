package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.config.BeaconHealthBarManager;
import com.zenavia.zenBattle.config.GameSettings;
import com.zenavia.zenBattle.config.MessageManager;
import com.zenavia.zenBattle.config.TitleManager;
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
    private final MessageManager messageManager;
    private final TitleManager titleManager;
    private final BeaconHealthBarManager beaconHealthBarManager;
    private final GameSettings settings;

    public BeaconDamageListener(GameManager gameManager, MessageManager messageManager, TitleManager titleManager, BeaconHealthBarManager beaconHealthBarManager, GameSettings settings) {
        this.gameManager = gameManager;
        this.messageManager = messageManager;
        this.titleManager = titleManager;
        this.beaconHealthBarManager = beaconHealthBarManager;
        this.settings = settings;
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
            player.playSound(clicked, Sound.ENTITY_VILLAGER_NO, 1f, 1.5f);
            player.sendMessage(messageManager.get("team.own-beacon-denied"));
            return;
        }

        boolean destroyed = target.damageBeacon(settings.damagePerHit());
        beaconHealthBarManager.update(target, settings.beaconMaxHealth(), Bukkit.getOnlinePlayers());

        float pitch = 1.0f + (1.0f - (float) target.getBeaconHealth() / settings.beaconMaxHealth());
        player.playSound(clicked, Sound.ENTITY_WITHER_HURT, 1f, pitch);
        Bukkit.broadcast(messageManager.get("game.beacon-damaged", Map.of("team", target.getName(), "health", String.valueOf(target.getBeaconHealth()))));

        if (destroyed) {
            game.onBeaconDestroyed(target);
            Team winner = game.getOtherTeam(target);
            Bukkit.broadcast(messageManager.get("game.victory", Map.of("team", winner.getName())));

            titleManager.showToAll(Bukkit.getOnlinePlayers(),
                    messageManager.get("game.victory-title"),
                    messageManager.get("game.victory-subtitle", Map.of("team", winner.getName())));

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
