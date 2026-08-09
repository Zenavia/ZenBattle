package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.kits.Kit;
import com.zenavia.zenBattle.kits.KitManager;
import com.zenavia.zenBattle.kits.KitMenu;
import com.zenavia.zenBattle.kits.KitMenuHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitMenuListener implements Listener {
    private final KitMenu kitMenu;
    private final KitManager kitManager;

    public KitMenuListener(KitMenu kitMenu, KitManager kitManager) {
        this.kitMenu = kitMenu;
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof KitMenuHolder)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Kit kit = kitMenu.kitFromSlot(event.getSlot());
        if (kit == null) return;

        kitManager.setKit(player.getUniqueId(), kit);
        player.sendMessage(Component.text("Kit sélectionné : ", NamedTextColor.GREEN)
                .append(Component.text(kit.getDisplayName(), NamedTextColor.YELLOW)));
        player.closeInventory();
    }
}
