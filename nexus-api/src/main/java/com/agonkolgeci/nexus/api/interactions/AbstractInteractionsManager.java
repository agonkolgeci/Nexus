package com.agonkolgeci.nexus.api.interactions;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractInteractionsManager<I extends AbstractPlugin> extends PluginManager<I> {

    @NotNull private final Map<ItemStack, PlayerInteractCallback> interactions;

    public AbstractInteractionsManager(@NotNull I instance) {
        super(instance);

        this.interactions = new HashMap<>();
    }

    @NotNull
    public ItemStack handle(@NotNull ItemStack itemStack, @NotNull PlayerInteractCallback interaction) {
        interactions.put(itemStack, interaction);

        return itemStack;
    }

    @Nullable
    protected PlayerInteractCallback retrieveOrNull(@NotNull ItemStack interactedItem) {
        return interactions.entrySet().stream().filter(e -> e.getKey().isSimilar(interactedItem)).findFirst().map(Map.Entry::getValue).orElse(null);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        @NotNull final Player player = event.getPlayer();
        @NotNull final ItemStack itemStack = event.getItem();

        @Nullable final PlayerInteractCallback interaction = retrieveOrNull(itemStack);
        if(interaction instanceof PlayerItemInteractCallback) {
            event.setCancelled(true);

            ((PlayerItemInteractCallback) interaction).onInteract(player, itemStack);
        }
    }

    @EventHandler
    public void onPlayerDrop(@NotNull PlayerDropItemEvent event) {
        @NotNull final Player player = event.getPlayer();
        @NotNull final ItemStack itemStack = event.getItemDrop().getItemStack();

        @Nullable final PlayerInteractCallback interaction = retrieveOrNull(itemStack);
        if(interaction instanceof PlayerItemInteractCallback) {
            event.setCancelled(true);

            ((PlayerItemInteractCallback) interaction).onInteract(player, itemStack);
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        @NotNull final Player player = (Player) event.getWhoClicked();
        @NotNull final Inventory inventory = event.getClickedInventory();
        @Nullable final ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null) return;

        @NotNull final InventoryAction action = event.getAction();
        @NotNull final ClickType clickType = event.getClick();
        final int slot = event.getRawSlot();

        @Nullable final PlayerInteractCallback interaction = retrieveOrNull(itemStack);
        if(interaction instanceof PlayerInventoryClickCallback) {
            event.setCancelled(true);

            ((PlayerInventoryClickCallback) interaction).onClick(player, inventory, itemStack, action, clickType, slot);
        }
    }

}
