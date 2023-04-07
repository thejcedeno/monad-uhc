package us.jcedeno.anmelden.bukkit.teams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

import lombok.extern.log4j.Log4j2;

/**
 * A class to test the TeamManager class.
 * 
 * @see TeamManager
 * @see Team
 * 
 * @author thejcedeno
 */
@Log4j2
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
        assertEquals(3, team.allMembers().size());
    }

    @Test
    @DisplayName("Creates a team manager, adds a few test teams and serializes and prints out serialized to console")
    public void test_teamManagerSerialization_success() {

        int i = 50;

        while (i++ < 50) {
            teamManager.createTeam(UUID.randomUUID(), UUID.randomUUID());
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();

        var json = gson.toJson(teamManager);

        assertTrue(json.length() > 2, "The serialized output is valid");
        System.out.println(String.format("Your serialized teamManager is: \n%s", json));

    }

}