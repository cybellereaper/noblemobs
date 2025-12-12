package vsctemplatemc.bazaar;

import java.util.UUID;

/**
 * Player-driven marketplace listing.
 */
public record BazaarListing(UUID id, String sellerName, String itemKey, int quantity, int unitPrice) {

    public BazaarListing reduce(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        return new BazaarListing(id, sellerName, itemKey, quantity - amount, unitPrice);
    }
}
