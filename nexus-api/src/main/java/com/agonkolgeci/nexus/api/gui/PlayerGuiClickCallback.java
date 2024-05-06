package com.agonkolgeci.nexus.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PlayerGuiClickCallback {

    void onClick(@NotNull Player targetPlayer, @NotNull ItemStack itemStack, @NotNull InventoryAction action, @NotNull ClickType clickType, int slot);

}
