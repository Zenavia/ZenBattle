package com.zenavia.zenBattle.listener;

import com.zenavia.zenBattle.game.GameManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BeaconBreakListener implements Listener {
    private final GameManager gameManager;

    public BeaconBreakListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() == Material.BEACON){
            event.setCancelled(true);
        }
    }
}
