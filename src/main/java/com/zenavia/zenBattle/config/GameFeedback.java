package com.zenavia.zenBattle.config;

import com.zenavia.zenBattle.game.Game;
import com.zenavia.zenBattle.game.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GameFeedback {
    private final MessageManager messages;
    private final TitleManager titles;
    private final BeaconHealthBarManager healthBars;
    private final GameSettings settings;

    public GameFeedback(MessageManager messages, TitleManager titles,
                        BeaconHealthBarManager healthBars, GameSettings settings) {
        this.messages = messages;
        this.titles = titles;
        this.healthBars = healthBars;
        this.settings = settings;
    }

    public void playerJoined(Player player, Team team) {
        player.sendMessage(messages.get("team.joined", Map.of("team", team.getName())));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }

    public void alreadyStarted(Player player) {
        player.sendMessage(messages.get("game.already-started"));
    }

    public void noArena(Player player) {
        player.sendMessage(messages.get("game.no-arena"));
    }

    public void notEnoughPlayer(Game game) {
        Bukkit.broadcast(messages.get("game.not-enough-players", Map.of("min-players", allPlayers(game).toString(), "max-players", String.valueOf(settings.minPlayersToStart()))));
    }

    public void ownBeaconDenied(Player player) {
        player.sendMessage(messages.get("team.own-beacon-denied"));
    }

    public void countdownStarted(int seconds) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        }

        titles.showToAll(Bukkit.getOnlinePlayers(),
                Component.text(""),
                messages.get("game.countdown-start", Map.of("seconds", String.valueOf(seconds))),
                false);
    }

    public void gameStarted() {
        Bukkit.broadcast(messages.get("game.starting"));
        titles.showToAll(Bukkit.getOnlinePlayers(),
                messages.get("game.starting-title"),
                messages.get("game.starting-subtitle"),
                true);
    }

    public void beaconHit(Team target, Location hitLocation) {
        int maxHealth = settings.beaconMaxHealth();
        healthBars.update(target, maxHealth, Bukkit.getOnlinePlayers());

        Bukkit.broadcast(messages.get("game.beacon-damaged",
                Map.of("team", target.getName(), "health", String.valueOf(target.getBeaconHealth()))));

        float pitch = 1.0f + (1.0f - (float) target.getBeaconHealth() / maxHealth);
        hitLocation.getWorld().playSound(hitLocation, Sound.BLOCK_ANVIL_LAND, 1f, pitch);
        hitLocation.getWorld().spawnParticle(Particle.CRIT, hitLocation.toCenterLocation(), 15, 0.3, 0.3, 0.3, 0.1);
    }

    public void victory(Team winner, Location beaconLocation) {
        Bukkit.broadcast(messages.get("game.victory", Map.of("team", winner.getName())));

        titles.showToAll(Bukkit.getOnlinePlayers(),
                messages.get("game.victory-title"),
                messages.get("game.victory-subtitle", Map.of("team", winner.getName())),
                true);

        beaconLocation.getWorld().playSound(beaconLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        beaconLocation.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, beaconLocation.toCenterLocation(), 40, 0.5, 0.5, 0.5, 0.2);
    }

    public void gameStoped(){
        Bukkit.broadcast(Component.text("Partie arrêtée par un administrateur.", NamedTextColor.RED));
    }

    public void gameReset() {
        healthBars.clearAll(Bukkit.getOnlinePlayers());
        Bukkit.broadcast(messages.get("game.reset"));
    }

    private AtomicInteger allPlayers(Game game) {
        Set<UUID> all = new HashSet<>(game.getTeamA().getPlayers());
        all.addAll(game.getTeamB().getPlayers());
        AtomicInteger count = new AtomicInteger();
        all.forEach(uuid -> {;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                count.getAndIncrement();
            }
        });
        return count;
    }
}
