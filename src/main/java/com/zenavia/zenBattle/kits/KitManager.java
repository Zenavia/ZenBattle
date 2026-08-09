package com.zenavia.zenBattle.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitManager {
    private final Map<UUID, Kit> chosenKits = new HashMap<>();

    public void setKit(UUID uuid, Kit kit) {
        chosenKits.put(uuid, kit);
    }

    public Kit getKit(UUID uuid) {
        return chosenKits.getOrDefault(uuid, Kit.GUERRIER); // kit par défaut si pas choisi
    }

    public void clear(UUID uuid) {
        chosenKits.remove(uuid);
    }

    public void clearAll() {
        chosenKits.clear();
    }
}
