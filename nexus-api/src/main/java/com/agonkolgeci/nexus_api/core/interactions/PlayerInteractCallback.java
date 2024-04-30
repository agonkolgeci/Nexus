package com.agonkolgeci.nexus_api.core.interactions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PlayerInteractCallback {

    public void onPlayerInteract(@NotNull Player player, @NotNull ItemStack interactedItem);

}
