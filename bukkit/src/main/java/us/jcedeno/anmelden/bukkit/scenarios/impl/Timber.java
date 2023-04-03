package us.jcedeno.anmelden.bukkit.scenarios.impl;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.ListenerScenario;

/**
 * A scenario that allows players to break entire trees by breaking one log.
 * The algorithm will stop after a maximum depth of 3 on each sub-transversal.
 * 
 * @author thejcedeno
 */
@Scenario(name = "Timber", description = "Break one log and the whole tree breaks", ui = Material.OAK_LOG)
public class Timber extends ListenerScenario {
    /**
     * The maximum depth of the tree to break.
     */
    private static final int MAX_GLOBAL_DEPTH = 3;
    /**
     * The maximum depth of the sub-transversals of the tree to break.
     */
    private static final int MAX_SUB_DEPTH = 2;
    /**
     * The set of all the log types.
     */
    private static final Set<Material> LOG_TYPES = EnumSet.of(
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG);

    /**
     * Default constructor required by the autoConfig system.
     * 
     * @param name        The name of the scenario.
     * @param description The description of the scenario.
     * @param material    The material to use as the scenario icon.
     */
    public Timber(String name, String description, Material material) {
        super(name, description, material);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        breakTree(e.getBlock(), e.getPlayer());
    }

    /**
     * Breaks all the logs of a tree from a given starting block.
     * Only logs of the same type as the starting block will be broken.
     * The algorithm will stop after a maximum depth of 3 on each sub-transversal.
     *
     * @param block  The starting block of the tree to break.
     * @param player The player breaking the tree.
     */
    private void breakTree(Block block, Player player) {
        // Get the type of log in the starting block.
        var logType = block.getType();
        // If the starting block is not a log, return.
        if (!LOG_TYPES.contains(logType)) {
            return;
        }
        // Create a queue to hold the blocks to visit, and add the starting block to it.
        var queue = new LinkedList<Block>();
        // Create a set to keep track of the blocks already visited, and add the
        // starting block to it.
        var visited = new HashSet<Block>();

        queue.add(block);
        visited.add(block);
        // Set the global depth to 0.
        var globalDepth = 0;
        // While there are still blocks to visit in the queue.
        while (!queue.isEmpty()) {
            // Get the next block in the queue.
            var currentBlock = queue.poll();
            // Break the block.
            currentBlock.breakNaturally(new ItemStack(Material.AIR), true);
            // Check all the adjacent blocks for logs of the same type, and add them to the
            // queue if they have not been visited.
            for (var face : BlockFace.values()) {
                var neighbor = currentBlock.getRelative(face);

                if (!visited.contains(neighbor) && neighbor.getType() == logType) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
            // If the global depth is less than 3, also check diagonally adjacent blocks.
            if (globalDepth < MAX_GLOBAL_DEPTH) {
                for (var face : BlockFace.values()) {
                    for (var diagonal : BlockFace.values()) {
                        // Skip checking the same direction twice.
                        if (diagonal == face) {
                            continue;
                        }

                        var neighbor = currentBlock.getRelative(face).getRelative(diagonal);

                        if (!visited.contains(neighbor) && neighbor.getType() == logType) {
                            queue.add(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            } else {
                // If the global depth is equal to 3, limit the depth of the transversal to 2.
                var subQueue = new LinkedList<Block>();
                var subVisited = new HashSet<Block>();

                // Iterate over each blockface and diagonal blockface to get all possible
                // diagonals.
                for (var face : BlockFace.values()) {
                    for (var diagonal : BlockFace.values()) {
                        // Skip checking the same direction twice.
                        if (diagonal == face) {
                            continue;
                        }
                        // Get the neighbour in the diagonal direction.
                        var neighbour = currentBlock.getRelative(face).getRelative(diagonal);
                        // If the neighbour has not already been visited and is of the same type as the
                        // logType, add it to the queue.
                        if (!visited.contains(neighbour) && !subVisited.contains(neighbour)
                                && neighbour.getType() == logType) {
                            subQueue.add(neighbour);
                            subVisited.add(neighbour);
                        }
                    }
                }

                int subDepth = 0;
                // Keep breaking the logs in the diagonal directions up to a depth of 2.
                while (!subQueue.isEmpty()) {
                    var subBlock = subQueue.poll();
                    subBlock.breakNaturally(new ItemStack(Material.AIR), true);
                    for (var face : BlockFace.values()) {
                        for (var diagonal : BlockFace.values()) {
                            if (diagonal == face) {
                                continue;
                            }
                            // Get the neighbour in the diagonal direction.
                            var neighbour = subBlock.getRelative(face).getRelative(diagonal);
                            if (!visited.contains(neighbour) && !subVisited.contains(neighbour)
                                    && neighbour.getType() == logType) {
                                subQueue.add(neighbour);
                                subVisited.add(neighbour);
                            }
                        }
                    }
                    subDepth++;
                    // If the current depth is greater than 2, break out of the loop.
                    if (subDepth > MAX_SUB_DEPTH) {
                        break;
                    }
                }
            }

            globalDepth++;
        }
    }

}
