package us.jcedeno.anmelden.bukkit.teams;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import us.jcedeno.anmelden.bukkit.teams.models.Team;
import us.jcedeno.anmelden.bukkit.teams.models.TeamInvite;

/**
 * This class is a singleton that manages all the the team objects and
 * interactions between those objects.
 * 
 * This class is responsible for:
 * - Creating teams
 * - Removing teams
 * - Adding members to teams
 * - Removing members from teams
 * - Sending team requests and handle the requests system
 * 
 * 
 * @author thejcedeno
 */
@NoArgsConstructor
@AllArgsConstructor
public class TeamManager {
    // A map to keep track of all teams that exist
    protected volatile Map<UUID, Team> teams = new ConcurrentHashMap<>();
    // Lookup table, for constant lookup time.
    protected volatile Map<UUID, Team> playerTeamLookup = new ConcurrentHashMap<>();
    // Invites map, key is the invitee's UUID , and the value is team invite object
    protected volatile Map<UUID, TeamInvite> invites = new ConcurrentHashMap<>();

    /**
     * Team invites system, composed of:
     * - /team invite <player> - executable from team leader only
     * - /team <accept/join:reject/deny> <teamLeaderName> - executable from any
     * player
     * 
     */

    /**
     * A function that takes two valid OfflinePlayers and creates a team with the
     * first one as the leader and the second one as a member.
     * This function also needs to update the player's team lookup table.
     * 
     * @param leader The leader of the team.
     * @param member The member(s) of the team.
     * 
     * @return The team that was created.
     */
    public Team createTeam(UUID leader, UUID... member) {
        var team = Team.leaderAndMemberTeam(leader, member);

        teams.put(team.getTeamId(), team);
        playerTeamLookup.put(leader, team);

        if (member != null)
            for (var m : member)
                playerTeamLookup.put(m, team);

        return team;
    }

    /*
     * A function that takes a valid OfflinePlayer and removes the team that the
     * player is the leader of.
     * 
     * @param leader The leader of the team.
     */
    public void removeTeam(UUID teamId) {
        var team = teams.get(teamId);

        teams.remove(teamId);
        playerTeamLookup.remove(team.getLeader());
        team.getMembers().forEach(playerTeamLookup::remove);
        // TODO: Fire Bukkit Event in higher imlementation.
    }

    /**
     * A function that takes a valid OfflinePlayer and creates a team with the
     * player as the leader.
     * 
     * @param leader The leader of the team.
     * 
     * @return The team that was created.
     */
    public Team team(UUID teamId) {
        return teams.get(teamId);
    }

    /**
     * A function that takes a valid OfflinePlayer and creates a team with the
     * player as the leader.
     * 
     * @param leader The leader of the team.
     * 
     * @return The team that was created.
     */
    public Team teamByPlayer(UUID playerId) {
        return playerTeamLookup.get(playerId);
    }

    /**
     * @return wether or not the player is in a team.
     */
    public boolean hasTeam(UUID playerId) {
        return playerTeamLookup.containsKey(playerId);
    }

    /**
     * @return wether or not the player is in a team.
     */
    public ArrayList<Team> teams() {
        return new ArrayList<>(teams.values());
    }

    /**
     * @return A list of all the teams.
     */
    public void addMember(UUID teamId, UUID member) {
        var team = teams.get(teamId);

        team.getMembers().add(member);
        playerTeamLookup.put(member, team);
        // TODO: Fire Bukkit Event in higher imlementation.

        // Notify all members of the team that a new member has joined.
        getOnlineTeamMembers(team).forEach(teamMember -> teamMember.sendMessage(
                miniMessage().deserialize("<green>" + Bukkit.getPlayer(member).getName() + " has joined the team.")));
    }

    /**
     * @return A list of all the teams.
     */
    public void removeMember(UUID teamId, UUID member) {
        var team = teams.get(teamId);

        team.getMembers().remove(member);
        playerTeamLookup.remove(member);

        // Notify all members of the team that a member has been removed from the team.
        getOnlineTeamMembers(team).forEach(teamMember -> teamMember.sendMessage(
                miniMessage().deserialize("<red>" + Bukkit.getPlayer(member).getName() + " has left the team.")));
    }

    /**
     * @return A list of all the teams.
     */
    private List<Player> getOnlineTeamMembers(Team team) {
        return team.allMembers().stream().map(Bukkit::getPlayer).filter(player -> player != null)
                .collect(Collectors.toList());
    }

    /**
     * @return A list of all the teams.
     */
    public void disbandTeam(Team team, Player player) {
        if (team.getLeader() != player.getUniqueId()) {
            player.sendMessage(miniMessage().deserialize("<red>You are not the leader of this team."));
            return;
        }

        getOnlineTeamMembers(team).forEach(member -> member.sendMessage(
                miniMessage().deserialize("<red>Your team has been disbanded by <white>" + player.getName() + ".")));

        removeTeam(team.getTeamId());
    }

    /**
     * Team Invite logic starts here
     */

    /**
     * A function that takes a sender player and a target player and sends a team
     * invite to the target player.
     * 
     * @param sender The player that is sending the invite.
     * @param target The player that is receiving the invite.
     * @param team   The team that the sender is inviting the target to.
     * 
     */
    public void sendTeamInvite(final Player sender, final Player target, final Team team) {
        // Check that the target player is not the sender.
        if (sender.getUniqueId().compareTo(target.getUniqueId()) == 0) {
            sender.sendMessage(miniMessage().deserialize("<red>You can't invite yourself to a team."));
            return;
        }
        // Check that player is actually the team leader.
        if (team.getLeader().compareTo(sender.getUniqueId()) != 0) {
            sender.sendMessage(miniMessage().deserialize("<red>You are not the leader of this team."));
            return;
        }
        // Check target player doesn't already have a team.
        if (hasTeam(target.getUniqueId())) {
            sender.sendMessage(miniMessage().deserialize("<red>This player is already in a team."));
            return;
        }
        // Check player doesn't already have an invite.
        if (invites.containsKey(target.getUniqueId())) {
            sender.sendMessage(miniMessage().deserialize("<red>This player already has an invite."));
            return;
        }
        sender.sendMessage(miniMessage()
                .deserialize("<white>You have invited <green>" + target.getName() + "<white> to join your team."));
        // Send the invite
        invites.put(target.getUniqueId(), TeamInvite.forTeam(team));
        target.sendMessage(miniMessage().deserialize("<white>You have been invited to join <green>" + team.getTeamName()
                + "<white> by <green>" + sender.getName() + "<white>."));
        target.sendMessage(miniMessage().deserialize("<white>Type <green>/team accept " + sender.getName()
                + "<white> to accept the invite or <red>/team reject " + sender.getName() + "<white> to reject it."));
    }

    /**
     * A function that accepts a team invite if present.
     * 
     * @param acceptor The player that is accepting the invite.
     * @param inviter  The player that sent the invite.
     */
    public void acceptTeamInvite(Player acceptor, Player inviter) {
        // Check that the target player is not the sender.
        if (acceptor.getUniqueId().compareTo(inviter.getUniqueId()) == 0) {
            acceptor.sendMessage(miniMessage().deserialize("<red>You can't accept an invite from yourself."));
            return;
        }
        // Check target player doesn't already have a team.
        if (hasTeam(acceptor.getUniqueId())) {
            acceptor.sendMessage(miniMessage().deserialize("<red>You already are a member of a team."));
            return;
        }

        var invite = invites.get(acceptor.getUniqueId());
        // Some sanity checks
        if (invite == null) {
            acceptor.sendMessage(miniMessage().deserialize("<red>You don't have any pending invites."));
            return;
        }
        if (invite.getInviterTeam().getLeader().compareTo(inviter.getUniqueId()) != 0) {
            acceptor.sendMessage(miniMessage()
                    .deserialize("<red>You don't have any pending invites from " + inviter.getName() + "."));
            return;
        }
        // Add the player to the team
        addMember(invite.getInviterTeam().getTeamId(), acceptor.getUniqueId());
        // Invalidate the invite
        invites.remove(acceptor.getUniqueId());

        acceptor.sendMessage(miniMessage().deserialize("<white>You have accepted the invite from <green>"
                + inviter.getName() + "<white> to join their team."));
        inviter.sendMessage(
                miniMessage().deserialize("<green>" + acceptor.getName() + "<white> has accept your invite."));
    }

    /**
     * Rejects a team invite.
     * 
     * @param rejector The player that is rejecting the invite.
     * @param inviter  The player that sent the invite.
     */
    public void rejectTeamInvite(Player rejector, Player inviter) {
        // Check that the target player is not the sender.
        if (rejector.getUniqueId().compareTo(inviter.getUniqueId()) == 0) {
            rejector.sendMessage(miniMessage().deserialize("<red>You can't reject an invite from yourself."));
            return;
        }
        // Check target player doesn't already have a team.
        if (hasTeam(rejector.getUniqueId())) {
            rejector.sendMessage(miniMessage().deserialize("<red>You already are a member of a team."));
            return;
        }

        var invite = invites.get(rejector.getUniqueId());
        // Some sanity checks
        if (invite == null) {
            rejector.sendMessage(miniMessage().deserialize("<red>You don't have any pending invites."));
            return;
        }
        if (invite.getInviterTeam().getLeader().compareTo(inviter.getUniqueId()) != 0) {
            rejector.sendMessage(miniMessage()
                    .deserialize("<red>You don't have any pending invites from " + inviter.getName() + "."));
            return;
        }
        // Invalidate the invite
        invites.remove(rejector.getUniqueId());

        rejector.sendMessage(miniMessage().deserialize(
                "<white>You have rejected the invite from <red>" + inviter.getName() + "<white> to join their team."));
        inviter.sendMessage(
                miniMessage().deserialize("<red>" + rejector.getName() + "<white> has rejected your invite."));
    }

    /**
     * Team Invite logic ends here
     */

    /**
     * A method that sends a message to all team members.
     * 
     * @param sender The player that is sending the message.
     * @param msg    The message to send.
     */
    public void sendTeamMessage(Player sender, String msg) {
        var team = this.teamByPlayer(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(miniMessage().deserialize("<red>You are not in a team."));
            return;
        }

        this.getOnlineTeamMembers(team).forEach(player -> player.sendMessage(
                miniMessage().deserialize("<white>[Team] <green>" + sender.getName() + "<white>: <#f3dbff>" + msg)));

    }

    public void kickPlayerFromTeam(Player sender, @Nullable Player player) {
        throw new RuntimeException("Not implemented yet.");
    }

}
