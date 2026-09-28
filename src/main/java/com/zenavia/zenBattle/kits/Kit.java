package com.zenavia.zenBattle.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Kit {
    ARCHER("Archer", Material.BOW) {
        @Override
        public ItemStack[] buildItems() {
            return new ItemStack[] {
                    new ItemStack(Material.BOW),
                    new ItemStack(Material.ARROW, 32),
                    new ItemStack(Material.LEATHER_CHESTPLATE)
            };
        }
    },
    GUERRIER("Guerrier", Material.IRON_SWORD) {
        @Override
        public ItemStack[] buildItems() {
            return new ItemStack[] {
                    new ItemStack(Material.IRON_SWORD),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_LEGGINGS)
            };
        }
    },
    DEFENSEUR("Défenseur", Material.SHIELD) {
        @Override
        public ItemStack[] buildItems() {
            return new ItemStack[] {
                    new ItemStack(Material.STONE_SWORD),
                    new ItemStack(Material.SHIELD),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_HELMET)
            };
        }
    };

    private final String displayName;
    private final Material icon;

    Kit(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public Material getIcon() { return icon; }

    public abstract ItemStack[] buildItems();
}
