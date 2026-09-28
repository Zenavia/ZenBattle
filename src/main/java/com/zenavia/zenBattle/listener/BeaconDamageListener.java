package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.arena.Arena;
import com.zenavia.zenBattle.config.ConfigManager;
import com.zenavia.zenBattle.config.GameFeedback;
import com.zenavia.zenBattle.game.Game;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.game.Team;
import com.zenavia.zenBattle.teleport.TeleportPointManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

public class BeaconDamageListener implements Listener {

    private final GameManager gameManager;
    private final GameFeedback feedback;
    private final ConfigManager configManager;
    private final TeleportPointManager teleportPointManager;
    private final Plugin plugin;

    public BeaconDamageListener(GameManager gameManager, GameFeedback feedback, ConfigManager configManager, TeleportPointManager teleportPointManager, Plugin plugin) {
        this.gameManager = gameManager;
        this.feedback = feedback;
        this.configManager = configManager;
        this.teleportPointManager = teleportPointManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.BEACON) return;

        if (gameManager.getGame().getState() == GameState.PLAYING && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        Game game = gameManager.getGame();
        if (game.getState() != GameState.PLAYING) return;

        Location clicked = event.getClickedBlock().getLocation();
        Team target = matchTeamByBeacon(game, clicked);
        if (target == null) return;


        Team playerTeam = game.getTeamOfPlayer(player.getUniqueId());
        if (playerTeam == null) return;
        if (playerTeam == target) {
            feedback.ownBeaconDenied(player);
            return;
        }

        teleportAttacker(player, target, playerTeam);

        boolean destroyed = target.damageBeacon(configManager.getSettings().damagePerHit());
        feedback.beaconHit(target, clicked, game.getOtherTeam(target).getOnlinePlayers());

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

    /**
     * Téléporte un joueur attaquant vers un point de téléportation "aléatoire".
     *
     * @param attacker     le joueur à téléporter
     * @param target       l'équipe dont le beacon a été touché
     * @param attackerTeam l'équipe de l'attaquant
     */
    private void teleportAttacker(Player attacker, Team target, Team attackerTeam) {
        Arena arena = gameManager.getCurrentArena();
        if (arena == null) {
            plugin.getLogger().warning("[TP DEBUG] Arena null !");
            return;
        }

        var points = attackerTeam == gameManager.getGame().getTeamA() ? arena.getTeleportPointsTeamB() : arena.getTeleportPointsTeamA();
        plugin.getLogger().info("[TP DEBUG] near=" + points.near().size() + " mid=" + points.mid().size() + " far=" + points.far().size());
        Location destination = teleportPointManager.resolveTeleportPoint(target, points);
        plugin.getLogger().info("[TP DEBUG] destination=" + destination);
        if (destination == null) return;

        attacker.teleport(destination);
        feedback.playerRepositioned(attacker);
    }
}
