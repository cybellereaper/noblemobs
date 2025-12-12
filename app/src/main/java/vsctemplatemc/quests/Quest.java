package vsctemplatemc.quests;

import java.util.List;
import java.util.UUID;

/**
 * Definition for a quest with simple objectives and textual rewards.
 */
public record Quest(UUID id, String name, String description, List<QuestObjective> objectives, List<String> rewards) {
}
