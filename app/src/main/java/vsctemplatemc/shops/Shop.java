package vsctemplatemc.shops;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a curated list of tradeable items for a merchant NPC. Stock is tracked
 * locally so that multiple shops can manage their own supply independently.
 */
public class Shop {

    private final String ownerName;
    private final String displayName;
    private final Map<String, ShopItem> stock = new LinkedHashMap<>();
    private final java.util.UUID ownerId;

    public Shop(String ownerName, String displayName) {
        this(null, ownerName, displayName);
    }

    public Shop(java.util.UUID ownerId, String ownerName, String displayName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.displayName = displayName;
    }

    public Shop addOrUpdateItem(String itemKey, int price, int quantity) {
        stock.put(itemKey, new ShopItem(itemKey, price, quantity));
        return this;
    }

    public boolean purchase(String itemKey, int quantity) {
        ShopItem item = stock.get(itemKey);
        if (item == null || item.quantity() < quantity) {
            return false;
        }

        stock.put(itemKey, item.reduce(quantity));
        return true;
    }

    public Map<String, ShopItem> viewStock() {
        return Collections.unmodifiableMap(stock);
    }

    public String ownerName() {
        return ownerName;
    }

    public String displayName() {
        return displayName;
    }

    public java.util.UUID ownerId() {
        return ownerId;
    }
}
