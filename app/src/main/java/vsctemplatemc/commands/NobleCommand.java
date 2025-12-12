package vsctemplatemc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import vsctemplatemc.bazaar.BazaarListing;
import vsctemplatemc.bazaar.BazaarService;
import vsctemplatemc.citizens.CitizenRegistry;
import vsctemplatemc.quests.QuestProgress;
import vsctemplatemc.quests.QuestService;
import vsctemplatemc.shops.ShopService;

/**
 * Primary command for inspecting NPC systems.
 */
public class NobleCommand implements CommandExecutor, TabCompleter {

    private final CitizenRegistry citizenRegistry;
    private final ShopService shopService;
    private final QuestService questService;
    private final BazaarService bazaarService;

    public NobleCommand(CitizenRegistry citizenRegistry, ShopService shopService, QuestService questService,
                        BazaarService bazaarService) {
        this.citizenRegistry = citizenRegistry;
        this.shopService = shopService;
        this.questService = questService;
        this.bazaarService = bazaarService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "NobleMobs subsystems: citizens, shops, quests, bazaar");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "citizens" -> sendCitizens(sender);
            case "shops" -> sendShops(sender);
            case "quests" -> sendQuests(sender);
            case "bazaar" -> sendBazaar(sender);
            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand");
        }
        return true;
    }

    private void sendCitizens(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "Citizens: " + String.join(", ", citizenRegistry.listNames()));
    }

    private void sendShops(CommandSender sender) {
        String description = shopService.getShops().values().stream()
                .map(shop -> shop.displayName() + " (by " + shop.ownerName() + ")")
                .collect(Collectors.joining(ChatColor.GRAY + " | " + ChatColor.AQUA));
        sender.sendMessage(ChatColor.GREEN + "Shops: " + description);
    }

    private void sendQuests(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view quest progress.");
            return;
        }
        UUID playerId = player.getUniqueId();
        Optional<QuestProgress> progress = questService.getProgress(playerId);
        if (progress.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No active quest. Talk to an innkeeper!");
            return;
        }
        QuestProgress questProgress = progress.get();
        sender.sendMessage(ChatColor.GOLD + "Active quest: " + questProgress.quest().name());
        questProgress.quest().objectives().forEach(objective -> {
            int value = questProgress.snapshot().getOrDefault(objective.key(), 0);
            sender.sendMessage(ChatColor.GRAY + " - " + objective.description() + ChatColor.AQUA + " [" + value
                    + "/" + objective.targetAmount() + "]");
        });
    }

    private void sendBazaar(CommandSender sender) {
        String lines = bazaarService.getListings().entrySet().stream()
                .map(entry -> entry.getValue().stream().findFirst()
                        .map(BazaarListing::unitPrice)
                        .map(price -> entry.getKey() + " from " + entry.getValue().size() + " seller(s) @ " + price)
                        .orElse(entry.getKey() + " (out of stock)"))
                .collect(Collectors.joining(ChatColor.GRAY + " | " + ChatColor.AQUA));
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Bazaar: " + lines);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("citizens", "shops", "quests", "bazaar");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("shops")) {
            return new ArrayList<>(shopService.getShops().keySet());
        }
        return List.of();
    }
}
