package us.jcedeno.anmelden.bukkit.scenarios.impl;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

/**
 * A scenario that automatically smelts ores and food.
 * 
 * TODO: Add a config file to allow the user to change the xp multiplier.
 * 
 * @author thejcedeno.
 */
@Log4j2
public class Cutclean extends BaseScenario implements Listener {

    /**
     * Scenario properties and constants
     */
    private @Getter @Setter int ironXp = 1;
    private @Getter @Setter int goldXp = 2;
    private @Getter @Setter int sandXp = 1;
    private @Getter @Setter int netheriteXp = 5;
    private static Random random = new Random();

    /**
     * Static constructor for the Cutclean scenario.
     */
    public static Cutclean create() {
        return new Cutclean("Cutclean", "All ores and food are automatically smelted");
    }

    /**
     * Constructor for the Cutclean scenario.
     * 
     * @param name        The name of the scenario.
     * @param description The description of the scenario.
     */
    protected Cutclean(String name, String description) {
        super(name, description);
        this.init();
    }

    /**
     * OPTIONAL
     */
    @Override
    public void init() {
        log.info("[☑] Cutclean scenario initialized");
    }

    /**
     * OPTIONAL
     */
    @Override
    public void enable() {
        log.info("[✅] Cutclean scenario enabled");
    }

    /**
     * OPTIONAL
     */
    @Override
    public void disable() {
        log.info("[❌] Cutclean scenario disabled");
    }

    /**
     * All the cutclean listeners
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {

        final var block = e.getBlock();
        final var player = e.getPlayer();
        final var itemInHand = player.getInventory().getItemInMainHand();
        var extras = 0;
        var extraGold = 0;

        if (itemInHand.getType() == Material.AIR || itemInHand.containsEnchantment(Enchantment.SILK_TOUCH))
            return;
        switch (block.getType()) {
            case IRON_ORE: {
                if (!block.getDrops(itemInHand, player).isEmpty()) {

                    final int fortune = fortuneMultiplier(itemInHand);
                    e.setDropItems(false);

                    // 70% chance of getting xp
                    if (random.nextDouble() <= 0.70) {
                        e.setExpToDrop(1);

                    }

                    dropCenter(new ItemStack(Material.IRON_INGOT, (fortune + extras)), block.getLocation());

                }
                break;
            }
            case GOLD_ORE: {
                if (!block.getDrops(itemInHand, player).isEmpty()) {
                    final int fortune = fortuneMultiplier(itemInHand);

                    e.setDropItems(false);

                    e.setExpToDrop(fortune);

                    dropCenter(new ItemStack(Material.GOLD_INGOT, (fortune + extras + extraGold)), block.getLocation());
                }
                break;
            }
            case SAND:
                if (!player.isSneaking())
                    break;

                if (!block.getDrops(itemInHand, player).isEmpty()) {
                    final int fortune = fortuneMultiplier(itemInHand);
                    e.setDropItems(false);

                    // 10% chance of getting xp
                    if (random.nextInt(5) + 1 <= 1 * fortune) {
                        e.setExpToDrop(sandXp);

                    }

                    dropCenter(new ItemStack(Material.GLASS, fortune), block.getLocation());
                }
                break;
            default:
                break;
        }

    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        var stack = e.getEntity().getItemStack();
        var type = stack.getType();
        if (type == Material.POTATO) {
            stack.setType(Material.BAKED_POTATO);
        } else if (type == Material.ANCIENT_DEBRIS) {
            stack.setType(Material.NETHERITE_SCRAP);
        } else if (type == Material.KELP) {
            stack.setType(Material.DRIED_KELP);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        switch (e.getEntityType()) {
            case COW:
            case CHICKEN:
            case PIG:
            case SHEEP:
            case SALMON:
            case MUSHROOM_COW:
            case HOGLIN:
            case COD:
            case RABBIT:
                e.getDrops().forEach(it -> {
                    if (it.getType().isEdible()) {
                        final Material cookedType = Material
                                .getMaterial("COOKED_" + it.getType().toString().toUpperCase());
                        if (cookedType != null)
                            it.setType(cookedType);
                    }
                });
                break;

            default:
                break;

        }

    }

    private static void summonXPOrb(Location loc, int amount) {
        loc.getWorld().spawn(loc, ExperienceOrb.class).setExperience(amount);

    }

    // Method that ensures ores don't fly like in many other servers
    private static Location dropCenter(ItemStack itemStack, Location location) {
        Location centeredLocation = new Location(location.getWorld(), location.getBlockX() + 0.5,
                location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
        Item item = location.getWorld().dropItem(centeredLocation, itemStack);
        item.setVelocity(new Vector(0.0, 0.1, 0.0));
        return centeredLocation;
    }

    private static int fortuneMultiplier(final ItemStack itemstack) {
        if (itemstack.getType() == Material.AIR || !itemstack.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            return 1;
        final int fortuneLevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        int bonus = random.nextInt(fortuneLevel);
        if (bonus == 0)
            bonus = 1;
        return bonus;
    }

    private static boolean isTool(Material material) {
        return material.toString().contains("PICKAXE") || material.toString().contains("AXE")
                || material.toString().contains("SHOVEL") || material.toString().contains("HOE");
    }

}
