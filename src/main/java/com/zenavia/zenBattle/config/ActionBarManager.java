package com.zenavia.zenBattle.config;

import com.zenavia.zenBattle.game.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

public class ActionBarManager {
    private final MessageManager messages;

    public ActionBarManager(MessageManager messages) {
        this.messages = messages;
    }

    public void send(Player player, Team team) {
        Component component = messages.get("game.action-bar", Map.of(
                "team", team.getName(),
                "health", String.valueOf(team.getBeaconHealth())
        ));
        player.sendActionBar(component);
    }
}
