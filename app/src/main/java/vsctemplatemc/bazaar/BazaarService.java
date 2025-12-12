package vsctemplatemc.bazaar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Basic bazaar implementation that supports multiple listings for the same item. Prices
 * are static per listing but the service returns listings sorted by best price first so
 * that callers can implement "cheapest available" behaviour.
 */
public class BazaarService {

    private final Map<String, List<BazaarListing>> listingsByItem = new LinkedHashMap<>();

    public BazaarListing createListing(String itemKey, String sellerName, int quantity, int unitPrice) {
        BazaarListing listing = new BazaarListing(UUID.randomUUID(), sellerName, itemKey, quantity, unitPrice);
        listingsByItem.computeIfAbsent(itemKey, key -> new ArrayList<>()).add(listing);
        listingsByItem.get(itemKey).sort((a, b) -> Integer.compare(a.unitPrice(), b.unitPrice()));
        return listing;
    }

    public Optional<BazaarListing> purchaseCheapest(String itemKey, int quantity) {
        List<BazaarListing> listings = listingsByItem.getOrDefault(itemKey, List.of());
        for (int i = 0; i < listings.size(); i++) {
            BazaarListing listing = listings.get(i);
            if (listing.quantity() >= quantity) {
                BazaarListing updated = listing.reduce(quantity);
                if (updated.quantity() == 0) {
                    listings.remove(i);
                } else {
                    listings.set(i, updated);
                }
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public Map<String, List<BazaarListing>> getListings() {
        return Collections.unmodifiableMap(listingsByItem);
    }
}
