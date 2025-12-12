package vsctemplatemc.citizens;

import java.time.Instant;
import java.util.UUID;

/**
 * Simple immutable definition of a citizen. Real entity attachment is intentionally
 * left out so the profile can be mapped to different NPC frameworks.
 */
public record NpcProfile(UUID id, String name, Role role, String persona, Instant createdAt) {
}
