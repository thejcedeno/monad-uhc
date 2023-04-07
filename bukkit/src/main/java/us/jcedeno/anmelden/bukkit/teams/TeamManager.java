package us.jcedeno.anmelden.bukkit.teams;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import us.jcedeno.anmelden.bukkit.teams.models.Team;

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
    protected Map<UUID, Team> teams = new HashMap<>();
    // Lookup table, for constant lookup time.
    protected Map<UUID, Team> playerTeamLookup = new HashMap<>();

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

    /*
     * @return A list of all the teams.
     */
    public ArrayList<Team> teams() {
        return new ArrayList<>(teams.values());
    }

    /*
     * @return A list of all the teams.
     */
    public void addMember(UUID teamId, UUID member) {
        var team = teams.get(teamId);

        team.getMembers().add(member);
        playerTeamLookup.put(member, team);
        // TODO: Fire Bukkit Event in higher imlementation.
    }

    /*
     * @return A list of all the teams.
     */
    public void removeMember(UUID teamId, UUID member) {
        var team = teams.get(teamId);

        team.getMembers().remove(member);
        playerTeamLookup.remove(member);
    }

    private List<Player> getOnlineTeamMembers(Team team) {
        return team.allMembers().stream().map(Bukkit::getPlayer).filter(player -> player != null)
                .collect(Collectors.toList());
    }

    public void disbandTeam(Team team, Player player) {
        if (team.getLeader() != player.getUniqueId()) {
            player.sendMessage(miniMessage().deserialize("<red>You are not the leader of this team."));
            return;
        }

        getOnlineTeamMembers(team).forEach(member -> member.sendMessage(
                miniMessage().deserialize("<red>Your team has been disbanded by <white>" + player.getName() + ".")));

        removeTeam(team.getTeamId());
    }

}
