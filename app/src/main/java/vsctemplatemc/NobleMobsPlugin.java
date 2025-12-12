package vsctemplatemc;

import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import vsctemplatemc.bazaar.BazaarService;
import vsctemplatemc.citizens.CitizenRegistry;
import vsctemplatemc.commands.NobleCommand;
import vsctemplatemc.quests.QuestListener;
import vsctemplatemc.quests.QuestService;
import vsctemplatemc.shops.ShopService;

/**
 * Primary entry point for the NobleMobs plugin. The plugin bootstraps lightweight
 * systems for NPC citizens, shops, quests, and a player-driven bazaar. Each system
 * stays in-memory for simplicity but exposes clear APIs that can be persisted later.
 */
public class NobleMobsPlugin extends JavaPlugin {

    private CitizenRegistry citizenRegistry;
    private ShopService shopService;
    private QuestService questService;
    private BazaarService bazaarService;

    @Override
    public void onEnable() {
        citizenRegistry = new CitizenRegistry();
        shopService = new ShopService();
        questService = new QuestService();
        bazaarService = new BazaarService();

        bootstrapSampleContent();

        NobleCommand command = new NobleCommand(citizenRegistry, shopService, questService, bazaarService);
        PluginCommand pluginCommand = Objects.requireNonNull(getCommand("noblemobs"), "Command registration failed");
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new QuestListener(questService), this);
        getLogger().info("NobleMobs systems initialized with starter content.");
    }

    @Override
    public void onDisable() {
        getLogger().info("NobleMobs data cleared from memory.");
    }

    private void bootstrapSampleContent() {
        citizenRegistry.createInnkeeper("Aria Rivers", "Greets travelers and tracks quest givers.");
        citizenRegistry.createMerchant("Eldon Sparks", "Trades rare curiosities for emeralds.");

        shopService.createShop("Aria Rivers", "Traveler's Provisions")
                .addOrUpdateItem("campfire", 4, 9)
                .addOrUpdateItem("cooked_beef", 2, 32);

        shopService.createShop("Eldon Sparks", "Curio Exchange")
                .addOrUpdateItem("ender_pearl", 12, 8)
                .addOrUpdateItem("amethyst_shard", 3, 24);

        questService.registerQuestSamples();

        bazaarService.createListing("iron_ingot", "PlayerBazaar", 64, 2);
        bazaarService.createListing("golden_carrot", "FarmerBazaar", 16, 5);
    }
}
