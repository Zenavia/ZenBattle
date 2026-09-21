package com.zenavia.zenBattle.config;

import com.zenavia.zenBattle.game.Team;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconHealthBarManager {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<String, BossBar> bars = new HashMap<>(); // clé = nom de la team

    public void update(Team team, int maxHealth, List<Player> viewers) {
        BossBar bar = bars.computeIfAbsent(team.getName(), name ->
                BossBar.bossBar(
                        miniMessage.deserialize("<red>Beacon " + name + "</red>"),
                        1.0f,
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS
                )
        );
        viewers.forEach(p -> p.showBossBar(bar));

        float progress = Math.max(0f, (float) team.getBeaconHealth() / maxHealth);
        bar.progress(progress);
    }

    public void clearAll(Iterable<? extends Player> viewers) {
        bars.values().forEach(bar -> viewers.forEach(p -> p.hideBossBar(bar)));
        bars.clear();
    }
}
