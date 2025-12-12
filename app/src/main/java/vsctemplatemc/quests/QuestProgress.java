package vsctemplatemc.quests;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tracks per-objective counts for a quest. Thread safety is not enforced because Bukkit
 * executes gameplay logic on the main thread by default.
 */
public class QuestProgress {

    private final Quest quest;
    private final Map<String, Integer> progressByObjective = new LinkedHashMap<>();

    public QuestProgress(Quest quest) {
        this.quest = quest;
        quest.objectives().forEach(objective -> progressByObjective.put(objective.key(), 0));
    }

    public Quest quest() {
        return quest;
    }

    public int advance(String objectiveKey, int amount) {
        int current = progressByObjective.getOrDefault(objectiveKey, 0);
        int next = Math.min(current + amount, quest.objectives().stream()
                .filter(obj -> obj.key().equals(objectiveKey))
                .findFirst()
                .map(QuestObjective::targetAmount)
                .orElse(current));
        progressByObjective.put(objectiveKey, next);
        return next;
    }

    public boolean isComplete() {
        return quest.objectives().stream()
                .allMatch(obj -> progressByObjective.getOrDefault(obj.key(), 0) >= obj.targetAmount());
    }

    public Map<String, Integer> snapshot() {
        return Collections.unmodifiableMap(progressByObjective);
    }
}
