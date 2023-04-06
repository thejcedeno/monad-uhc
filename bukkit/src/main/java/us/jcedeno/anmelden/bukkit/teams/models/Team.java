package us.jcedeno.anmelden.bukkit.teams.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.joml.Math;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;

/**
 * A dataclass to hold all th information about a team.
 * 
 * @author thejcedeno
 */
@Data
@Builder
@AllArgsConstructor(staticName = "create")
public class Team {
    private @NonNull @Default final UUID teamId = UUID.randomUUID();
    private @NonNull String displayName;
    private @NonNull String teamName;
    private @NonNull UUID leader;
    private @NonNull @Default Collection<UUID> members = new ArrayList<>();

    /**
     * A function that returns all the members of the team. All UUIDs in the members
     * collection + the leader UUID in a single collection.
     * 
     * @return A collection of all the members of the team.
     */
    public Collection<UUID> allMembers() {
        var allMembers = Lists.newArrayList(members);
        allMembers.add(leader);
        return allMembers;
    }

    /**
     * A function that takes a valid OfflinePlayer and adds them to the team that
     * the player is the leader of.
     * 
     * @param leader The leader of the team.
     * @param member The member of the team.
     */
    public static Team leaderOnlyTeam(UUID leader) {
        return Team.builder().leader(leader).teamName(leader.toString().split("-")[0] + "'s Team")
                .displayName("" + Math.random() * 100L).build();
    }

    /**
     * A function that takes two valid OfflinePlayers and creates a team with the
     * first one as the leader and the second one as a member.
     * 
     * @param leader The leader of the team.
     * @param member The member of the team.
     * 
     * @return The team that was created.
     */
    public static Team leaderAndMemberTeam(UUID leader, UUID... member) {
        return Team.builder().leader(leader).members(Lists.newArrayList(member))
                .teamName(leader.toString().split("-")[0] + "'s Team").displayName("" + Math.random() * 100L).build();
    }

    public static Team emptyTeam() {
        // WILL THROW EXCEPTION
        return Team.builder().teamName("Empty Team").displayName("Empty Team").build();
    }

}
