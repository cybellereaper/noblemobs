package vsctemplatemc.shops;

/**
 * Stock entry within a shop. Immutable so updates create new instances and can be
 * safely shared in snapshots.
 */
public record ShopItem(String itemKey, int price, int quantity) {

    public ShopItem reduce(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        return new ShopItem(itemKey, price, quantity - amount);
    }
}
