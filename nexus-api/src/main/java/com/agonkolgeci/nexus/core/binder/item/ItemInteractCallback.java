package com.agonkolgeci.nexus.core.binder.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemInteractCallback extends InteractCallback {

    void onInteract(@NotNull Player player, @NotNull ItemStack itemStack);

}
