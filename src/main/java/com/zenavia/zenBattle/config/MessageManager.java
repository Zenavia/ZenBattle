package com.zenavia.zenBattle.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Map;

public class MessageManager {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private YamlConfiguration messages;
    private String prefix;

    public MessageManager(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) plugin.saveResource("messages.yml", false);
        this.messages = YamlConfiguration.loadConfiguration(file);
        this.prefix = messages.getString("prefix", "");
    }

    public Component get(String path, Map<String, String> placeholders) {
        String raw = messages.getString(path);
        if (raw == null) {
            return miniMessage.deserialize("<red>Message manquant: " + path + "</red>");
        }
        raw = raw.replace("<prefix>", prefix);
        for (var entry : placeholders.entrySet()) {
            raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return miniMessage.deserialize(raw);
    }

    public Component get(String path) {
        return get(path, Map.of());
    }

    public void reload(Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(file);
        this.prefix = messages.getString("prefix", "");
    }
}
