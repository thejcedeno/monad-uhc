package us.jcedeno.anmelden.bukkit.teams.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.teams.TeamManager;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.UUID;

@CommandContainer
@Log4j2
public class TeamCommands {
    public TeamManager teamManager = MonadUHC.instance().getTeamManager();

    private static final String DEFAULT_TEAM_NAME = "DefaultTeamName";

    @CommandMethod("team")
    public void team(final @NonNull CommandSender sender) {
        sender.sendMessage("Team command");
    }

    @CommandMethod("team create [teamName]")
    @CommandDescription("If the  sender doesn't have a team, it create it for them")
    public void createTeam(final @NonNull Player player,
            @Argument(value = "teamName", defaultValue = DEFAULT_TEAM_NAME) @Greedy String teamName) {
        // If team name is actually default team name, then generate a random team name
        if (teamName.equals(DEFAULT_TEAM_NAME)) {
            teamName = "Team " + UUID.randomUUID().toString().split("-")[0];
            log.info("Team name was default, generating random team name: {}", teamName);
        }
        if (teamManager.hasTeam(player.getUniqueId())) {
            player.sendMessage(miniMessage().deserialize(String.format("<red>You already have a team.")));
            return;
        }
        // We need to check if the player already, is they do, then we exit early.
        player.sendMessage(miniMessage()
                .deserialize(String.format("<green>Team <white><bold>%s</bold></white> has been created.", teamName)));

        var newTeam = teamManager.createTeam(player.getUniqueId());

        newTeam.setTeamName(teamName);
        newTeam.setDisplayName(teamName);
    }

    /**
     * Disbands the team of the player.
     * 
     */

    @CommandMethod("team disband")
    @CommandDescription("Disbands the team of the player."	)
    public void disbandTeam(final @NonNull Player player) {
        if (!teamManager.hasTeam(player.getUniqueId())) {
            player.sendMessage(miniMessage().deserialize("<red>You don't have a team."));
            return;
        }
        var team = teamManager.teamByPlayer(player.getUniqueId());
        teamManager.disbandTeam(team, player);
        player.sendMessage(miniMessage().deserialize("<green>Your team has been disbanded."));
    }

    @CommandMethod("team invite <target>")
    @CommandDescription("Invites a player to your team.")
    public void playerInviteCommand(Player sender, @Argument("target") OfflinePlayer target) {
        if (!teamManager.hasTeam(sender.getUniqueId())) {
            sender.sendMessage(miniMessage().deserialize("<red>You don't have a team."));
            return;
        }
        if (teamManager.hasTeam(target.getUniqueId())) {
            sender.sendMessage(miniMessage().deserialize(String.format("<red>%s already has a team.", target.getName())));
            return;
        }

        sender.sendMessage(miniMessage().deserialize(String
                .format("<green>You have invited <white><bold>%s</bold></white> to your team.", target.getName())));

    }

    // Boiler plate code for the command framework.
    public TeamCommands(final @NonNull AnnotationParser<CommandSender> parser) {
    }

}
