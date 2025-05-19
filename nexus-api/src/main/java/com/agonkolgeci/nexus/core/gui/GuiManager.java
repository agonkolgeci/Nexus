package com.agonkolgeci.nexus.core.gui;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GuiManager extends PluginManager<NexusAPI> implements PluginAdapter, ListenerAdapter {

    @NotNull public static final Map<UUID, AbstractGui<?>> PLAYERS_GUI = new ConcurrentHashMap<>();

    public GuiManager(@NotNull NexusAPI instance) {
        super(instance);
    }

    @Override
    public void load() throws Exception {
        instance.getEventsManager().registerAdapter(this);
    }

    @Override
    public void unload() {
        instance.getEventsManager().unregisterAdapter(this);
    }

    public static void openGui(@NotNull Player player, @NotNull AbstractGui<?> playerGui) {
        playerGui.onOpen(player);
        player.openInventory(playerGui.getInventory());

        PLAYERS_GUI.put(player.getUniqueId(), playerGui);
    }

    public static void closeGui(@NotNull Player player) {
        PLAYERS_GUI.computeIfPresent(player.getUniqueId(), (uuid, abstractGui) -> {
            abstractGui.onClose(player);
            player.closeInventory();

            return null;
        });
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        closeGui(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;

        @NotNull final Inventory closedInventory = event.getInventory();

        PLAYERS_GUI.computeIfPresent(player.getUniqueId(), (uuid, abstractGui) -> {
            if(abstractGui.getInventory().equals(closedInventory)) {
                abstractGui.onClose(player);
            }

            return null;
        });
    }

}
