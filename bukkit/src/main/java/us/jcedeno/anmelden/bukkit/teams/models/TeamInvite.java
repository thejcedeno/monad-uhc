package us.jcedeno.anmelden.bukkit.teams.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A class that represents a team invite.
 * 
 * @author thejcedeno
 */
@Builder
@Data
@RequiredArgsConstructor(staticName = "forTeam")
@AllArgsConstructor(staticName = "create")
public class TeamInvite {
    private @Default Long sentTime = System.currentTimeMillis();
    private @NonNull Team inviterTeam;
}
