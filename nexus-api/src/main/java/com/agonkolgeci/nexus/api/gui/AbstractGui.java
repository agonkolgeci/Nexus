package com.agonkolgeci.nexus.api.gui;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractGui<I extends AbstractPlugin> extends PluginManager<I> {

    @NotNull protected final Component title;
    protected final int size;
    protected final boolean scalable;

    @NotNull protected final Inventory inventory;

    @Nullable private final AbstractGui<I> parent;

    @NotNull private final Map<ItemStack, PlayerGuiClickCallback> interactions;

    public AbstractGui(@NotNull I instance, @NotNull Component title, int size, boolean scalable, @Nullable AbstractGui<I> parent) {
        super(instance);

        this.title = title;
        this.size = size;
        this.scalable = scalable;

        this.inventory = instance.getServer().createInventory(null, size, LegacyComponentSerializer.legacySection().serialize(title.colorIfAbsent(NamedTextColor.DARK_GRAY)));

        this.parent = parent;

        this.interactions = new HashMap<>();
    }

    public AbstractGui(@NotNull I instance, @NotNull Component title, int size, boolean scalable) {
        this(instance, title, size, scalable, null);
    }

    public abstract void update(@NotNull Player player);

    public void onOpen(@NotNull Player player) {}
    public void onClose(@NotNull Player player) {}

    @NotNull
    protected ItemStack handleInteraction(@NotNull ItemStack itemStack, @NotNull PlayerGuiClickCallback interaction) {
        interactions.put(itemStack, interaction);

        return itemStack;
    }

    protected void handleBackward(@NotNull AbstractGuiManager<I> guiManager, int slot) {
        if(parent == null) throw new IllegalStateException("This gui doesn't have parent interface !");

        inventory.setItem(slot, handleInteraction(new ItemBuilder(Material.DARK_OAK_DOOR).displayName(Component.text("Revenir en arrière", NamedTextColor.DARK_GRAY)).build(), (targetPlayer, itemStack, action, clickType, slot1) -> guiManager.openGui(targetPlayer, parent)));
    }

}
