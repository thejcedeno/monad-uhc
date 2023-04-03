package us.jcedeno.anmelden.bukkit.scenarios.commands;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit._utils.fastinv.FastInv;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

/**
 * A class holding all the user-facing Scenario commands.
 * 
 * @author thejcedeno.
 */
@CommandContainer
public final class ScenarioCommands {

    @CommandMethod("scenario")
    public void enabledScenarios(final @NonNull CommandSender sender) {
        // Sender error message, in red, saying that there are no scenarios enabled.
        List<BaseScenario> enabledScenarios = MonadUHC.instance().getScenarioManager().enabledScenarios();

        if (enabledScenarios.isEmpty()) {
            sender.sendMessage(miniMessage().deserialize("<red>There are no scenarios enabled."));
            return;
        }

        if (sender instanceof Player player) {
            var inv = new FastInv(InventoryType.HOPPER, "Scenarios");

            enabledScenarios.forEach(scenario -> inv.addItem(getScenarioItem(scenario), e -> {
                e.getWhoClicked().sendMessage("You clicked on the scenario " + scenario.name());
            }));

            inv.open(player);
            return;
        }

        sender.sendMessage(miniMessage().deserialize("<green>Enabled Scenarios:"));
            MonadUHC.instance().getScenarioManager().enabledScenarios()
                    .forEach(scenario -> sender.sendMessage("- " + scenario.name()));
    }

    public static ItemStack getScenarioItem(BaseScenario scenario) {
        var item = new ItemStack(scenario.material());
        var meta = item.getItemMeta();
        
        meta.displayName(miniMessage().deserialize(scenario.name()));
        meta.lore(List.of(miniMessage().deserialize(scenario.description())));
        item.setItemMeta(meta);

        return item;
    }

    // REQUIRED BY CLOUD FRAMEWORK
    public ScenarioCommands(final @NonNull AnnotationParser<CommandSender> annotationParser) {
    }

}
