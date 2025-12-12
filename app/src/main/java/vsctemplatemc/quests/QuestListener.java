package vsctemplatemc.quests;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Minimal listener that ties gameplay hooks to quest progress. The hooks are small by
 * design so server owners can expand or replace them with their own events.
 */
public class QuestListener implements Listener {

    private final QuestService questService;

    public QuestListener(QuestService questService) {
        this.questService = questService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        questService.listQuests().stream().findFirst().ifPresent(quest ->
                questService.assignQuest(player.getUniqueId(), quest.id()));
    }

    @EventHandler
    public void onNpcInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        questService.recordObjective(playerId, "talk_innkeeper", 1);
        questService.recordObjective(playerId, "visit_market", 1);
    }
}
