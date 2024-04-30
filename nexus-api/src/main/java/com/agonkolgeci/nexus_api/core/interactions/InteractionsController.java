package com.agonkolgeci.nexus_api.core.interactions;

import com.agonkolgeci.nexus_api.NexusAPI;
import com.agonkolgeci.nexus_api.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InteractionsController extends PluginModule<NexusAPI> implements PluginController, ListenerAdapter {

    @NotNull private final Map<ItemStack, PlayerInteractCallback> objects;

    public InteractionsController(@NotNull NexusAPI instance) {
        super(instance);

        this.objects = new HashMap<>();
    }


    @Override
    public void load() {
        instance.getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        instance.getEventsController().unregisterEventAdapter(this);
    }

    @NotNull
    public ItemStack handleInteraction(@NotNull ItemStack itemStack, @NotNull PlayerInteractCallback interaction) {
        objects.put(itemStack, interaction);

        return itemStack;
    }

    @Nullable
    public Map.Entry<ItemStack, PlayerInteractCallback> retrieveInteraction(@NotNull ItemStack interactedItem) {
        return objects.entrySet().stream().filter(e -> e.getKey().isSimilar(interactedItem)).findFirst().orElse(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        @NotNull final Player player = event.getPlayer();
        @NotNull final ItemStack interactedItem = event.getItem();

        @Nullable final Map.Entry<ItemStack, PlayerInteractCallback> interaction = retrieveInteraction(interactedItem);
        if(interaction != null) {
            event.setCancelled(true);

            interaction.getValue().onPlayerInteract(player, interactedItem);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        @NotNull final Player player = (Player) event.getWhoClicked();

        @Nullable final Inventory clickedInventory =  event.getClickedInventory();
        if(clickedInventory == null || (clickedInventory.getType() == InventoryType.PLAYER && player.getGameMode() == GameMode.CREATIVE)) return;

        @NotNull final ItemStack interactedItem = event.getCurrentItem();

        @Nullable final Map.Entry<ItemStack, PlayerInteractCallback> interaction = retrieveInteraction(interactedItem);
        if(interaction != null) {
            event.setCancelled(true);

            interaction.getValue().onPlayerInteract(player, interactedItem);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrop(@NotNull PlayerDropItemEvent event) {
        @NotNull final ItemStack interactedItem = event.getItemDrop().getItemStack();

        @Nullable final Map.Entry<ItemStack, PlayerInteractCallback> interaction = retrieveInteraction(interactedItem);
        if(interaction != null) {
            event.setCancelled(true);
        }
    }
}
