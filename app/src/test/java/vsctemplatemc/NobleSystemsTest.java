package vsctemplatemc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vsctemplatemc.bazaar.BazaarService;
import vsctemplatemc.citizens.CitizenRegistry;
import vsctemplatemc.quests.QuestObjective;
import vsctemplatemc.quests.QuestProgress;
import vsctemplatemc.quests.QuestService;
import vsctemplatemc.shops.ShopService;

class NobleSystemsTest {

    @Test
    void citizensAreCategorizedByRole() {
        CitizenRegistry registry = new CitizenRegistry();
        registry.createInnkeeper("Aria", "Warm and inviting");
        registry.createMerchant("Eldon", "Trades curios");

        assertEquals(1, registry.findByRole(vsctemplatemc.citizens.Role.INNKEEPER).size());
        assertEquals(1, registry.findByRole(vsctemplatemc.citizens.Role.MERCHANT).size());
    }

    @Test
    void shopsTrackInventoryAndPurchases() {
        ShopService shops = new ShopService();
        shops.createShop("Eldon", "Curio Exchange").addOrUpdateItem("ender_pearl", 12, 5);

        assertTrue(shops.findByOwner("Eldon").orElseThrow().purchase("ender_pearl", 3));
        assertEquals(2, shops.findByOwner("Eldon").orElseThrow().viewStock().get("ender_pearl").quantity());
        assertFalse(shops.findByOwner("Eldon").orElseThrow().purchase("ender_pearl", 5));
    }

    @Test
    void citizensCanBindEntitiesAndReceiveShops() {
        CitizenRegistry registry = new CitizenRegistry();
        ShopService shops = new ShopService();
        UUID villagerId = UUID.randomUUID();

        var profile = registry.registerAndBind("Trader Tom", "Local vendor", vsctemplatemc.citizens.Role.MERCHANT, villagerId);
        shops.createShop(profile.id(), profile.name(), "Tom's Goods");

        assertEquals(profile, registry.findByEntity(villagerId).orElseThrow());
        assertTrue(shops.findByOwnerId(profile.id()).isPresent());
        assertNotNull(shops.findByOwner(profile.name()));
    }

    @Test
    void bazaarBuysFromCheapestListing() {
        BazaarService bazaar = new BazaarService();
        bazaar.createListing("iron_ingot", "SellerA", 16, 4);
        bazaar.createListing("iron_ingot", "SellerB", 16, 2); // Cheapest

        assertTrue(bazaar.purchaseCheapest("iron_ingot", 8).isPresent());
        int remaining = bazaar.getListings().get("iron_ingot").getFirst().quantity();
        assertEquals(8, remaining);
    }

    @Test
    void questsAdvanceAndComplete() {
        QuestService quests = new QuestService();
        var quest = quests.createQuest("Test", "", List.of(new QuestObjective("collect", "Collect items", 2)), List.of());
        UUID playerId = UUID.randomUUID();
        quests.assignQuest(playerId, quest.id());

        QuestProgress progress = quests.recordObjective(playerId, "collect", 1).orElseThrow();
        assertFalse(progress.isComplete());
        quests.recordObjective(playerId, "collect", 1);
        assertTrue(progress.isComplete());
    }
}
