package com.agonkolgeci.nexus.api.interactions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PlayerItemInteractCallback extends PlayerInteractCallback {

    void onInteract(@NotNull Player player, @NotNull ItemStack itemStack);

}
