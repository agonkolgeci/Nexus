package com.agonkolgeci.nexus.core.binder.item;

import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.core.binder.BinderManager;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class InteractionsBinder implements ListenerAdapter {

    public static final Map<ItemStack, InteractCallback> ITEMS = new ConcurrentHashMap<>();

    @NotNull private final BinderManager binderManager;

    public InteractionsBinder(@NotNull BinderManager binderManager) {
        this.binderManager = binderManager;
    }

    @NotNull
    public static ItemStack bind(ItemStack itemStack, InteractCallback itemInteract) {
        ITEMS.put(itemStack, itemInteract);
        return itemStack;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;

        @NotNull final Player player = event.getPlayer();
        @Nullable final ItemStack interactedItem = event.getItem();
        if(interactedItem == null) return;

        ITEMS.computeIfPresent(interactedItem, (targetItem, callback) -> {
            if(callback instanceof ItemInteractCallback itemInteractCallback) {
                event.setCancelled(true);
                itemInteractCallback.onInteract(player, interactedItem);
            }

            return callback;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        @NotNull final Player player = event.getPlayer();
        @NotNull final ItemStack droppedItem = event.getItemDrop().getItemStack();

        ITEMS.computeIfPresent(droppedItem, (targetItem, callback) -> {
            if(callback instanceof ItemInteractCallback itemInteractCallback) {
                event.setCancelled(true);

                itemInteractCallback.onInteract(player, droppedItem);
            }

            return callback;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof @NotNull final Player player)) return;
//        if(player.getGameMode() == GameMode.CREATIVE) return;

        @Nullable final Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory == null) return;

        @Nullable final ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;

        @NotNull final InventoryAction action = event.getAction();
        @NotNull final ClickType clickType = event.getClick();
        final int slot = event.getRawSlot();

        ITEMS.computeIfPresent(clickedItem, (itemStack, callback) -> {
            if(callback instanceof InventoryClickCallback inventoryClickCallback) {
                event.setCancelled(true);

                inventoryClickCallback.onClick(player, clickedInventory, clickedItem, action, clickType, slot);
            }

            return callback;
        });
    }

}
