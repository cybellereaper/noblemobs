package vsctemplatemc.citizens;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vsctemplatemc.shops.ShopService;

/**
 * Listens for right-click interactions to quickly promote existing entities into
 * citizens that can host shops and quests. Players can sneak-right-click with a
 * paper named "Citizen Contract" to convert villagers or fellow players into a
 * lightweight citizen profile.
 */
public class CitizenInteractionListener implements Listener {

    private static final String CONTRACT_NAME = ChatColor.GOLD + "Citizen Contract";

    private final CitizenRegistry citizenRegistry;
    private final ShopService shopService;

    public CitizenInteractionListener(CitizenRegistry citizenRegistry, ShopService shopService) {
        this.citizenRegistry = citizenRegistry;
        this.shopService = shopService;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }
        if (!isCitizenContract(player.getInventory().getItemInMainHand())) {
            return;
        }

        Entity target = event.getRightClicked();
        citizenRegistry.findByEntity(target.getUniqueId()).ifPresent(profile -> {
            player.sendMessage(ChatColor.YELLOW + "That entity is already a citizen: " + profile.name());
        });
        if (citizenRegistry.findByEntity(target.getUniqueId()).isPresent()) {
            return;
        }

        Role role = determineRole(target);
        String persona = buildPersona(role);
        var profile = citizenRegistry.registerAndBind(target.getName(), persona, role, target.getUniqueId());
        shopService.createShop(profile.id(), profile.name(), profile.name() + "'s Market");
        player.sendMessage(ChatColor.GREEN + "Created citizen " + profile.name() + " as a " + role.name().toLowerCase()
                + " and linked a shop.");
    }

    private boolean isCitizenContract(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.PAPER) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && CONTRACT_NAME.equals(meta.getDisplayName());
    }

    private Role determineRole(Entity entity) {
        if (entity instanceof Villager) {
            return Role.MERCHANT;
        }
        if (entity instanceof Player) {
            return Role.INNKEEPER;
        }
        return Role.GUIDE;
    }

    private String buildPersona(Role role) {
        return switch (role) {
            case INNKEEPER -> "Keeps notes for player-driven inns.";
            case MERCHANT -> "Trades custom wares as a player merchant.";
            case GUIDE -> "Shares tips and route markers.";
            case QUEST_GIVER -> "Hands out tasks from adventures.";
        };
    }
}
