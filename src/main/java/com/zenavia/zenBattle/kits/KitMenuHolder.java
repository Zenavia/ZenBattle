package com.zenavia.zenBattle.kits;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class KitMenuHolder implements InventoryHolder {
    private Inventory inventory;

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
