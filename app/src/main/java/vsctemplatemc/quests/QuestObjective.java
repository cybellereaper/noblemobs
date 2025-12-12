package vsctemplatemc.quests;

/**
 * Lightweight objective that can be triggered by arbitrary game actions.
 */
public record QuestObjective(String key, String description, int targetAmount) {
}
