package com.zenavia.zenBattle.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

public class TitleManager {

    public void show(Player player, Component title, Component subtitle) {
        Title t = Title.title(
                title,
                subtitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        player.showTitle(t);
    }

    public void showToAll(Iterable<? extends Player> players, Component title, Component subtitle) {
        players.forEach(p -> show(p, title, subtitle));
    }
}
