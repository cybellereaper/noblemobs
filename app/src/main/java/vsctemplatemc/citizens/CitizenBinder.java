package vsctemplatemc.citizens;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Interprets a lore-driven binder item that players can right-click with to turn
 * an entity into a registered citizen. The binder keeps parsing logic isolated
 * from Bukkit listeners for easier testing.
 */
public class CitizenBinder {

    private static final String DISPLAY_NAME = ChatColor.GOLD + "Citizen Binder";
    private static final String ROLE_PREFIX = "Role:";
    private static final String SHOP_PREFIX = "Shop:";
    private static final String PERSONA_PREFIX = "Persona:";

    public ItemStack createBinder(Role role, String shopName, String persona) {
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        meta.setDisplayName(DISPLAY_NAME);
        List<String> lore = new ArrayList<>();
        lore.add(ROLE_PREFIX + " " + role.name());
        if (shopName != null && !shopName.isBlank()) {
            lore.add(SHOP_PREFIX + " " + shopName);
        }
        if (persona != null && !persona.isBlank()) {
            lore.add(PERSONA_PREFIX + " " + persona);
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public boolean isBinder(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.NAME_TAG) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && DISPLAY_NAME.equals(meta.getDisplayName());
    }

    public Role roleFrom(ItemStack itemStack) {
        return parseLoreLine(itemStack, ROLE_PREFIX)
                .flatMap(this::parseRole)
                .orElse(Role.MERCHANT);
    }

    public Optional<String> shopName(ItemStack itemStack) {
        return parseLoreLine(itemStack, SHOP_PREFIX);
    }

    public String persona(ItemStack itemStack, String fallback) {
        return parseLoreLine(itemStack, PERSONA_PREFIX).orElse(fallback);
    }

    private Optional<String> parseLoreLine(ItemStack itemStack, String prefix) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            return Optional.empty();
        }
        return meta.getLore().stream()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()).trim())
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private Optional<Role> parseRole(String value) {
        try {
            return Optional.of(Role.valueOf(value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
