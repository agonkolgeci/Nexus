package com.agonkolgeci.nexus.core.binder.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface InventoryClickCallback extends InteractCallback {

    void onClick(@NotNull Player player, @NotNull Inventory inventory, @NotNull ItemStack itemStack, @NotNull InventoryAction action, @NotNull ClickType clickType, int slot);

}
