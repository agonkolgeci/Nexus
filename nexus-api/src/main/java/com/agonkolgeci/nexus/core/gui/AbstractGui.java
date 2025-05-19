package com.agonkolgeci.nexus.core.gui;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.core.binder.item.InventoryClickCallback;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class AbstractGui<I extends AbstractPlugin> {

    @NotNull protected final I instance;
    @Nullable protected final AbstractGui<I> parent;

    @NotNull protected final Component title;
    protected final int size;
    protected final boolean scalable;

    @NotNull protected final Inventory inventory;

    public AbstractGui(@NotNull I instance, @Nullable AbstractGui<I> parent, @NotNull Component title, int size, boolean scalable) {
        this.instance = instance;
        this.title = title;
        this.size = size;
        this.scalable = scalable;

        this.inventory = Bukkit.createInventory(null, size, title.colorIfAbsent(NamedTextColor.DARK_GRAY));

        this.parent = parent;
    }

    public AbstractGui(@NotNull I instance, @NotNull Component title, int size, boolean scalable) {
        this(instance, null, title, size, scalable);
    }

    public abstract void onOpen(@NotNull Player player);
    public abstract void onClose(@NotNull Player player);

    protected void addBackward(int slot) {
        if(parent == null) throw new IllegalStateException("Backward icon needs parent gui.");

        inventory.setItem(slot, InteractionsBinder.bind(new ItemBuilder(Material.DARK_OAK_DOOR).displayName(Component.text("Revenir en arrière", NamedTextColor.DARK_GRAY)).build(), (InventoryClickCallback) (targetPlayer, inventory, itemStack, action, clickType, slot1) -> {
            GuiManager.openGui(targetPlayer, parent);
        }));
    }

}
