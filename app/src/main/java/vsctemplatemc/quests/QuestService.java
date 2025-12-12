package vsctemplatemc.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Quest registry with minimal progress tracking for online players.
 */
public class QuestService {

    private final Map<UUID, Quest> quests = new LinkedHashMap<>();
    private final Map<UUID, QuestProgress> playerProgress = new LinkedHashMap<>();

    public Quest createQuest(String name, String description, List<QuestObjective> objectives, List<String> rewards) {
        Quest quest = new Quest(UUID.randomUUID(), name, description, List.copyOf(objectives), List.copyOf(rewards));
        quests.put(quest.id(), quest);
        return quest;
    }

    public void assignQuest(UUID playerId, UUID questId) {
        Quest quest = quests.get(questId);
        if (quest == null) {
            throw new IllegalArgumentException("Unknown quest: " + questId);
        }
        playerProgress.put(playerId, new QuestProgress(quest));
    }

    public Optional<QuestProgress> getProgress(UUID playerId) {
        return Optional.ofNullable(playerProgress.get(playerId));
    }

    public Optional<QuestProgress> recordObjective(UUID playerId, String objectiveKey, int amount) {
        QuestProgress progress = playerProgress.get(playerId);
        if (progress == null) {
            return Optional.empty();
        }
        progress.advance(objectiveKey, amount);
        return Optional.of(progress);
    }

    public List<Quest> listQuests() {
        return Collections.unmodifiableList(new ArrayList<>(quests.values()));
    }

    public void registerQuestSamples() {
        List<QuestObjective> discovery = List.of(
                new QuestObjective("talk_innkeeper", "Speak to the innkeeper in the capital.", 1),
                new QuestObjective("visit_market", "Inspect any bazaar listing.", 1));

        createQuest("World Welcome", "Ease players into the NobleMobs ecosystem", discovery,
                List.of("50 coins", "Traveler's Cloak"));

        List<QuestObjective> hunter = List.of(
                new QuestObjective("slay_raiders", "Defeat raiders near the village.", 5),
                new QuestObjective("recover_relic", "Return the stolen relic to Aria.", 1));

        createQuest("Village Protector", "Assist Aria in reclaiming the stolen relic", hunter,
                List.of("Rare charm", "Village renown"));
    }
}
