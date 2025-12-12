package vsctemplatemc.shops;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages NPC shops in-memory. Shops are keyed by their owner name for simplicity,
 * but in production you can swap this for NPC UUIDs from a dedicated NPC plugin.
 */
public class ShopService {

    private final Map<String, Shop> shopsByOwner = new LinkedHashMap<>();

    public Shop createShop(String ownerName, String displayName) {
        Shop shop = new Shop(ownerName, displayName);
        shopsByOwner.put(ownerName, shop);
        return shop;
    }

    public Optional<Shop> findByOwner(String ownerName) {
        return Optional.ofNullable(shopsByOwner.get(ownerName));
    }

    public Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(shopsByOwner);
    }
}
