package de.thorbenwillimek.timber;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class Timber extends JavaPlugin implements Listener {

    /**
     * Contains all wooden log materials.
     */
    private static final List<Material> WOODEN_LOGS = asList(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.DARK_OAK_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_DARK_OAK_LOG
    );

    @Override
    public void onEnable() {
        getServer()
                .getPluginManager()
                .registerEvents(this, this);
    }

    @EventHandler
    private void onWoodLogBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isSneaking()) {
            return;
        }

        Block brokenBlock = event.getBlock();
        if (isWoodenLog(brokenBlock)) {
            breakTrunk(brokenBlock);
        }
    }

    /**
     * Returns whether a given block is a wooden log block.
     *
     * @param block the block to validate
     * @return true if the block is a wooden log block - false otherwise
     */
    private static boolean isWoodenLog(Block block) {
        return WOODEN_LOGS.contains(block.getType());
    }

    private void breakTrunk(Block block) {
        if (!isWoodenLog(block)) {
            return;
        }

        Set<Block> adjacentWoodLogs = getAdjacentWoodLogs(block);
        block.breakNaturally();
        new BukkitRunnable() {
            @Override
            public void run() {
                adjacentWoodLogs.forEach(Timber.this::breakTrunk);
            }
        }.runTaskAsynchronously(this);
    }

    /**
     * For a given starting wooden log block, this determines the 3x3x3 cube of blocks surrounding the starting block.
     * Each wooden log block in this 3x3x3 cube will then be added to a set which gets returned eventually.
     *
     * @param block the starting block
     * @return all wooden log blocks in the surrounding 3x3x3 cube. If there are no wooden logs, this returns an empty set.
     */
    private Set<Block> getAdjacentWoodLogs(Block block) {
        if (!isWoodenLog(block)) {
            return Collections.emptySet();
        }

        // TODO: Make plugin useful for multiple worlds and make entire methode static
        World world = this.getServer().getWorlds().get(0);
        Set<Block> adjacentLogs = new HashSet<>();

        // TODO: This could be more efficient...
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block currentBlock = world.getBlockAt(block.getX() + x, block.getY() + y, block.getZ() + z);
                    if (isWoodenLog(currentBlock)) {
                        adjacentLogs.add(currentBlock);
                    }
                }
            }
        }

        return adjacentLogs;
    }


}