package us.jcedeno.anmelden.bukkit.whitelist.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.whitelist.WhitelistManager;

@CommandContainer
public class WhitelistCMD {

    @CommandMethod("whitelist list")
    public void listAll(final CommandSender sender) {
        var wl = this.manager().getWhitelist();

        if (wl.isEmpty()) {
            sender.sendMessage("Whitelist is empty");
            return;
        }

        // Send pretty message to sender
        sender.sendMessage(
                MiniMessage.miniMessage().deserialize(String.format("gold>Players in Whitelist (%s):", wl.size())));

        // TODO: Handle NPE on getOfflinePlayer
        wl.forEach(uuid -> sender.sendMessage(MiniMessage.miniMessage()
                .deserialize(String.format("<white> - %s</white>", Bukkit.getOfflinePlayer(uuid)))));
    }

    @CommandMethod("whitelist add <player>")
    public void addPlayer(final CommandSender sender, final @Argument("player") String offlinePlayerName) {
        var ofp = Bukkit.getOfflinePlayer(offlinePlayerName);
        // Shortcircut if playername doesn't exist.
        if (ofp == null || ofp.getUniqueId() == null) {
            sender.sendMessage(MiniMessage.miniMessage()
                    .deserialize(String.format("<red>Player %s not found", offlinePlayerName)));
            return;
        }
        // Shortcircut if player is already whitelisted.
        if (this.manager().getWhitelist().contains(ofp.getUniqueId())) {
            sender.sendMessage(MiniMessage.miniMessage()
                    .deserialize(String.format("<red>Player %s is already whitelisted", offlinePlayerName)));
            return;
        }
        this.manager().getWhitelist().add(ofp.getUniqueId());
        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize(String.format("<green>Added %s to whitelist", offlinePlayerName)));
    }

    @CommandMethod("whitelist remove <player>")
    public void removePlayer(final CommandSender sender, final @Argument("player") String offlinePlayerName) {
        var ofp = Bukkit.getOfflinePlayer(offlinePlayerName);
        // Shortcircut if playername doesn't exist.
        if (ofp == null || ofp.getUniqueId() == null) {
            sender.sendMessage(MiniMessage.miniMessage()
                    .deserialize(String.format("<red>Player %s not found", offlinePlayerName)));
            return;
        }
        // Shortcircut if player is already whitelisted.
        if (!this.manager().getWhitelist().contains(ofp.getUniqueId())) {
            sender.sendMessage(MiniMessage.miniMessage()
                    .deserialize(String.format("<red>Player %s is not whitelisted", offlinePlayerName)));
            return;
        }
        this.manager().getWhitelist().remove(ofp.getUniqueId());
        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize(String.format("<green>Removed %s from whitelist", offlinePlayerName)));
    }

    @CommandMethod("whitelist add-all")
    public void addAll(final CommandSender sender) {
        this.manager().getWhitelist().addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList());
        
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Added all online players to whitelist"));
    }

    @CommandMethod("whitelist clear")
    public void clearWhitelist(final CommandSender sender) {
        this.manager().getWhitelist().clear();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Whitelist cleared"));
    }

    @CommandMethod("whitelist <boolean>")
    public void toggleWhitelist(final CommandSender sender, final @Argument("boolean") boolean toggle) {
        if(toggle)
            this.manager().enableWhitelist();
        else
            this.manager().disableWhitelist();
            
        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize(String.format("<green>Whitelist is now %s", toggle ? "enabled" : "disabled")));
    }

    public WhitelistCMD(final AnnotationParser<CommandSender> parser) {
    }

    private WhitelistManager manager() {
        return MonadUHC.instance().getWhitelistManager();
    }

}
