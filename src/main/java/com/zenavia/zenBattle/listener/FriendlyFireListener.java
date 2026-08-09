package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.game.Game;
import com.zenavia.zenBattle.game.GameManager;
import com.zenavia.zenBattle.game.GameState;
import com.zenavia.zenBattle.game.Team;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class FriendlyFireListener implements Listener {
    private final GameManager gameManager;

    public FriendlyFireListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = resolveAttacker(event);
        if (attacker == null) return;
        if (attacker.equals(victim)) return;

        Game game = gameManager.getGame();
        if (game.getState() != GameState.PLAYING) return;

        Team attackerTeam = getTeamOfPlayer(game, attacker.getUniqueId());
        Team victimTeam = getTeamOfPlayer(game, victim.getUniqueId());

        if (attackerTeam == null || victimTeam == null) return;
        if (attackerTeam == victimTeam) {
            event.setCancelled(true);
        }
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) return player;
        if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player player) return player;
        return null;
    }

    private Team getTeamOfPlayer(Game game, UUID uuid) {
        if (game.getTeamA().getPlayers().contains(uuid)) return game.getTeamA();
        if (game.getTeamB().getPlayers().contains(uuid)) return game.getTeamB();
        return null;
    }
}
