package us.jcedeno.anmelden.bukkit.skins.commands;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.destroystokyo.paper.profile.ProfileProperty;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.skins.SkinToolApi;

@CommandContainer
public class SkinToolCommand {

    @CommandMethod("skin <targetPlayer> <skinSource> <skinVariant>")
    @CommandPermission("skintool.command.skin")
    public void skinCommand(CommandSender sender, @Argument("targetPlayer") Player targetPlayer,
            @Argument("skinSource") String skinSource,
            @Argument(value = "skinVariant", defaultValue = "original") String skinVariant) {

        CompletableFuture.supplyAsync(() -> {
            UUID id = null;
            Player onlinePlayer = Bukkit.getPlayer(skinSource);

            if (onlinePlayer != null && onlinePlayer.getUniqueId() != null) {
                id = onlinePlayer.getUniqueId();
            } else {
                id = SkinToolApi.getUserProfile(skinSource);
            }

            if (id != null) {
                if (skinVariant.equalsIgnoreCase("original")) {
                    var skin = SkinToolApi.getCurrentUserSkin(id, false);
                    Bukkit.getScheduler().runTask(MonadUHC.instance(),
                            () -> skinSwapper(targetPlayer, skin.getValue(), skin.getSignature()));
                } else {
                    SkinToolApi.getElseComputeSkins(id).whenComplete((skins, exception) -> {
                        if (exception != null) {
                            sender.sendMessage("Command ended exceptionally: " + exception.getMessage());
                            exception.printStackTrace();
                            return;
                        }

                        var skin = skins.stream().filter(s -> s.getName().equalsIgnoreCase(skinVariant))
                                .findFirst();
                        if (skin.isEmpty()) {
                            sender.sendMessage("Skin not found");
                            return;
                        }

                        var actualSkin = skin.get();
                        Bukkit.getScheduler().runTask(MonadUHC.instance(),
                                () -> skinSwapper(targetPlayer, actualSkin.getValue(), actualSkin.getSignature()));
                    });
                }
            }
            return false;
        });
    }

    /**
     * Function that swaps a player's skin for a different one.
     * 
     * @param player    The player to swap the skin for.
     * @param texture   The texture to apply to the player.
     * @param signature The signature of the texture.
     */
    public static void skinSwapper(Player player, String texture, String signature) {
        var profilePurpur = player.getPlayerProfile();
        profilePurpur.setProperty(new ProfileProperty(player.getName(), texture, signature));

        player.setPlayerProfile(profilePurpur);
    }

}
