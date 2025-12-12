package vsctemplatemc.shops;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages NPC shops in-memory. Shops are keyed by their owner name for simplicity,
 * but in production you can swap this for NPC UUIDs from a dedicated NPC plugin.
 */
public class ShopService {

    private final Map<String, Shop> shopsByOwnerName = new LinkedHashMap<>();
    private final Map<UUID, Shop> shopsByOwnerId = new LinkedHashMap<>();

    public Shop createShop(String ownerName, String displayName) {
        Shop shop = new Shop(ownerName, displayName);
        shopsByOwnerName.put(ownerName, shop);
        return shop;
    }

    public Shop createShop(UUID ownerId, String ownerName, String displayName) {
        Shop shop = new Shop(ownerId, ownerName, displayName);
        shopsByOwnerName.put(ownerName, shop);
        shopsByOwnerId.put(ownerId, shop);
        return shop;
    }

    public Optional<Shop> findByOwner(String ownerName) {
        return Optional.ofNullable(shopsByOwnerName.get(ownerName));
    }

    public Optional<Shop> findByOwnerId(UUID ownerId) {
        return Optional.ofNullable(shopsByOwnerId.get(ownerId));
    }

    public Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(shopsByOwnerName);
    }

    public Map<UUID, Shop> getShopsByOwnerId() {
        return Collections.unmodifiableMap(shopsByOwnerId);
    }
}
