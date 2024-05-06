package com.agonkolgeci.nexus.api.gui;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.players.events.PlayerLogoutEvent;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractGuiManager<I extends AbstractPlugin> extends PluginManager<I> {

    @NotNull protected final Map<Player, AbstractGui<I>> interfaces;

    public AbstractGuiManager(@NotNull I instance) {
        super(instance);

        this.interfaces = new HashMap<>();
    }

    public boolean hasPlayerGui(@NotNull Player player) {
        return interfaces.containsKey(player);
    }

    @Nullable
    public AbstractGui<I> retrievePlayerGui(@NotNull Player player) {
        return interfaces.getOrDefault(player, null);
    }

    public void openGui(@NotNull Player player, @NotNull AbstractGui<I> playerGui) {
        playerGui.update(player);

        player.openInventory(playerGui.getInventory());

        interfaces.put(player, playerGui);
    }

    public void closeGui(@NotNull Player player) {
        @Nullable final AbstractGui<I> playerGui = retrievePlayerGui(player);
        if(playerGui == null) return;

        player.getPlayer().closeInventory();

        interfaces.remove(player, playerGui);
    }

    @EventHandler
    public void onPlayerLogout(@NotNull PlayerLogoutEvent event) {
        @NotNull final Player player = event.getPlayer();
        if(hasPlayerGui(player)) {
            this.closeGui(player);
        }
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        @NotNull final Player player = (Player) event.getPlayer();
        @Nullable final AbstractGui<I> playerGui = retrievePlayerGui(player);
        if(playerGui == null) return;

        playerGui.onOpen(player);
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) return;

        @NotNull final Player player = (Player) event.getPlayer();
        @Nullable final AbstractGui<I> playerGui = retrievePlayerGui(player);
        if(playerGui == null) return;

        playerGui.onClose(player);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        @NotNull final Player player = (Player) event.getWhoClicked();
        @Nullable final AbstractGui<I> playerGui = retrievePlayerGui(player);
        if(playerGui == null) return;

        @NotNull final Inventory inventory = event.getClickedInventory();
        if(!playerGui.getInventory().equals(inventory)) return;

        @Nullable final ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null) return;

        @NotNull final InventoryAction action = event.getAction();
        @NotNull final ClickType clickType = event.getClick();
        final int slot = event.getRawSlot();

        @Nullable final PlayerGuiClickCallback interaction = playerGui.getInteractions().entrySet().stream().filter(e -> e.getKey().isSimilar(itemStack)).findFirst().map(Map.Entry::getValue).orElse(null);
        if(interaction != null) {
            if(!playerGui.isScalable()) {
                event.setCancelled(true);
            }

            interaction.onClick(player, itemStack, action, clickType, slot);
        }
    }

}
