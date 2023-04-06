package us.jcedeno.anmelden.bukkit.teams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class to test the TeamManager class.
 * 
 * @see TeamManager
 * @see Team
 * 
 * @author  thejcedeno
 */
public class TeamManagerTests {

    private TeamManager teamManager = new TeamManager();

    @Test
    @DisplayName("Given a request to register a new team, "
            + "when the request is valid, "
            + "then a new team is created with one leader and one team member.")
    public void teat_teamMananagerCreateTeam_success() {
        // Given
        var leader = UUID.randomUUID();
        var member = UUID.randomUUID();

        // When
        var team = teamManager.createTeam(leader, member);

        // Then
        assertEquals(leader, team.getLeader());
        assertEquals(1, team.getMembers().size());
        assertEquals(2, team.allMembers().size());
    }

    

}