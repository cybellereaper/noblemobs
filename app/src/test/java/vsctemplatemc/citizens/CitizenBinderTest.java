package vsctemplatemc.citizens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CitizenBinderTest {

    private CitizenBinder binder;

    @BeforeEach
    void setUp() {
        binder = new CitizenBinder();
    }

    @Test
    void detectsBinderByDisplayName() {
        ItemStack nameTag = baseBinder();
        assertTrue(binder.isBinder(nameTag));

        ItemStack wrongItem = mockStack("Not a binder", List.of());
        Mockito.when(wrongItem.getType()).thenReturn(Material.NAME_TAG);
        assertFalse(binder.isBinder(wrongItem));
    }

    @Test
    void parsesRoleAndShopFromLore() {
        ItemStack nameTag = mockStack(ChatColor.GOLD + "Citizen Binder", List.of("Role: INNKEEPER", "Shop: Cozy Beds"));
        Mockito.when(nameTag.getType()).thenReturn(Material.NAME_TAG);

        assertEquals(Role.INNKEEPER, binder.roleFrom(nameTag));
        assertEquals("Cozy Beds", binder.shopName(nameTag).orElseThrow());
    }

    @Test
    void defaultsToMerchantAndMissingShop() {
        ItemStack nameTag = baseBinder();
        assertEquals(Role.MERCHANT, binder.roleFrom(nameTag));
        assertTrue(binder.shopName(nameTag).isEmpty());
    }

    private ItemStack baseBinder() {
        return mockStack(ChatColor.GOLD + "Citizen Binder", List.of());
    }

    private ItemStack mockStack(String displayName, List<String> lore) {
        ItemMeta meta = Mockito.mock(ItemMeta.class);
        Mockito.when(meta.getDisplayName()).thenReturn(displayName);
        Mockito.when(meta.getLore()).thenReturn(lore);

        ItemStack stack = Mockito.mock(ItemStack.class);
        Mockito.when(stack.getItemMeta()).thenReturn(meta);
        Mockito.when(stack.getType()).thenReturn(Material.NAME_TAG);
        return stack;
    }
}
