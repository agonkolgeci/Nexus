package com.agonkolgeci.nexus.utils.render;

import com.google.common.base.Function;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InventoryUtils {

    public static <T> void fillSlots(@NotNull Inventory inventory, int[] slots, @NotNull List<T> values, @NotNull Function<T, ItemStack> generator) {
        for(int i = 0; i < values.size() && i < slots.length; i++) {
            inventory.setItem(slots[i], generator.apply(values.get(i)));
        }
    }

}
