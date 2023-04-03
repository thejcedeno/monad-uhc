package us.jcedeno.anmelden.bukkit.scenarios.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.ExperienceOrb.SpawnReason;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.ListenerScenario;

@Scenario(name = "HasteBoys", description = "Tools come pre-enchanted\nwith efficiency 3 and unbreaking 3.", ui = Material.DIAMOND_PICKAXE)
public class HasteyBoys extends ListenerScenario {

    public HasteyBoys(String name, String description, Material material) {
        super(name, description, material);
    }


    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        var result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        if (isTool(result.getType())) {
            ItemStack stack = result.clone();
            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(Enchantment.DIG_SPEED, 3, false);
            meta.addEnchant(Enchantment.DURABILITY, 3, false);
            stack.setItemMeta(meta);
            e.getInventory().setResult(stack);
        }

    }

    @EventHandler
    public void onGrindstone(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.GRINDSTONE) {
            var grindInv = (GrindstoneInventory) e.getInventory();
            var slot0 = grindInv.getContents()[0];
            var slot1 = grindInv.getContents()[1];
            if (hasEnchanments(slot0) || hasEnchanments(slot1)) {
                Bukkit.getScheduler().runTask(MonadUHC.instance(), ()->{
                    grindInv.getLocation().getNearbyEntitiesByType(ExperienceOrb.class, 1).forEach(it->{
                        if(it.getSpawnReason() == SpawnReason.GRINDSTONE)
                            it.setExperience(0);
                        
                    });

                });
            }
        }
    }

    private static boolean hasEnchanments(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta())
            return false;

        var meta = stack.getItemMeta();
        return meta.getEnchantLevel(Enchantment.DIG_SPEED) == 3 && meta.getEnchantLevel(Enchantment.DURABILITY) == 3;
    }

    private static boolean isTool(Material material) {
        var materialName = material.toString();
        return material != null && materialName.contains("AXE") || materialName.contains("SHOVEL")
                || materialName.contains("HOE") || materialName.contains("SHEARS");
    }

}
