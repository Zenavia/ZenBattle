package com.zenavia.zenBattle.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.*;
import org.bukkit.plugin.Plugin;

public class BarrierManager {

    private final Plugin plugin;

    public BarrierManager(Plugin plugin){
        this.plugin = plugin;
    }

    public void breakBarrier(Location corner1, Location corner2, Material barrierMaterial) {
        BarrierContext ctx = buildContext(corner1, corner2, barrierMaterial);
        if (ctx == null) return;

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(ctx.region().getWorld()).build()) {
            BlockTypeMask mask = new BlockTypeMask(editSession, ctx.material());
            editSession.replaceBlocks(ctx.region, mask, BlockTypes.AIR);
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur FAWE lors du break de la barrière : " + e.getMessage());
            return;
        }

        Location center = corner1.clone().add(corner2).multiply(0.5);
        corner1.getWorld().playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.7f);
        corner1.getWorld().spawnParticle(Particle.EXPLOSION, center, 5, 1, 1, 1, 0.1);
    }

    public void rebuildBarrier(Location corner1, Location corner2, Material barrierMaterial) {
        BarrierContext ctx = buildContext(corner1, corner2, barrierMaterial);
        if (ctx == null) return;

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(ctx.region().getWorld()).build()) {
            BlockTypeMask fillMask = new BlockTypeMask(editSession, BlockTypes.AIR, BlockTypes.WATER);
            editSession.replaceBlocks(ctx.region, fillMask, ctx.material().getDefaultState());
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur FAWE lors du rebuild de la barrière : " + e.getMessage());
        }
    }

    private record BarrierContext(CuboidRegion region, BlockType material) {}

    private BarrierContext buildContext(Location corner1, Location corner2, Material barrierMaterial) {
        if (corner1 == null || corner2 == null || barrierMaterial == null) return null;

        World bukkitWorld = corner1.getWorld();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(bukkitWorld);

        BlockVector3 min = BukkitAdapter.asBlockVector(corner1);
        BlockVector3 max = BukkitAdapter.asBlockVector(corner2);
        CuboidRegion region = new CuboidRegion(weWorld, min, max);

        BlockType material = BukkitAdapter.asBlockType(barrierMaterial);
        if (material == null) return null;

        return new BarrierContext(region, material);
    }
}
