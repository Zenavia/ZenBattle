package com.zenavia.zenBattle.kits;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenu {

    public void open(Player player) {
        KitMenuHolder holder = new KitMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 9, Component.text("Choisis ton kit", NamedTextColor.DARK_GRAY));
        holder.setInventory(inv);

        for (Kit kit : Kit.values()) {
            ItemStack item = new ItemStack(kit.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(kit.getDisplayName(), NamedTextColor.YELLOW));
            item.setItemMeta(meta);
            inv.setItem(slotFor(kit), item);
        }

        player.openInventory(inv);
    }

    private int slotFor(Kit kit) {
        return switch (kit) {
            case ARCHER -> 2;
            case GUERRIER -> 4;
            case DEFENSEUR -> 6;
        };
    }

    public Kit kitFromSlot(int slot) {
        return switch (slot) {
            case 2 -> Kit.ARCHER;
            case 4 -> Kit.GUERRIER;
            case 6 -> Kit.DEFENSEUR;
            default -> null;
        };
    }
}
