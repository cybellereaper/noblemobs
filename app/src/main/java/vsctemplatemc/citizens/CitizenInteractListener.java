package vsctemplatemc.citizens;

import java.time.Instant;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import vsctemplatemc.shops.ShopService;

/**
 * Handles binder interactions so players can right-click existing players or villagers
 * to register them as lightweight citizens and optionally attach a shop definition.
 */
public class CitizenInteractListener implements Listener {

    private final CitizenRegistry citizenRegistry;
    private final ShopService shopService;
    private final CitizenBinder binder;

    public CitizenInteractListener(CitizenRegistry citizenRegistry, ShopService shopService, CitizenBinder binder) {
        this.citizenRegistry = citizenRegistry;
        this.shopService = shopService;
        this.binder = binder;
    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (!binder.isBinder(itemStack)) {
            return;
        }

        Entity target = event.getRightClicked();
        if (!(target instanceof Player) && !(target instanceof Villager)) {
            return;
        }

        String targetName = target.getName();
        if (citizenRegistry.existsWithName(targetName)) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + targetName + ChatColor.GRAY + " is already registered.");
            return;
        }

        Role role = binder.roleFrom(itemStack);
        String persona = binder.persona(itemStack, "Bound in the wilds at " + Instant.now());
        citizenRegistry.registerNpc(targetName, persona, role);

        var shopName = binder.shopName(itemStack);
        shopName.ifPresent(shop -> shopService.createShop(targetName, shop));
        event.getPlayer().sendMessage(ChatColor.GREEN + "Linked citizen " + targetName + ChatColor.GRAY + " as "
                + role.name().toLowerCase() + shopName.map(name -> " with shop " + name).orElse(""));
        event.setCancelled(true);
    }
}
