package com.agonkolgeci.nexus_hub.core.gui.main;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.core.binder.item.InventoryClickCallback;
import com.agonkolgeci.nexus.core.gui.AbstractGui;
import com.agonkolgeci.nexus.core.gui.GuiManager;
import com.agonkolgeci.nexus.core.server.Game;
import com.agonkolgeci.nexus.utils.inventory.InventoryUtils;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class MainGui extends AbstractGui<NexusHub> {

    public static final int[] GAMES_SLOTS = new int[] {22,23,24,25,31,32,33,34};

    public MainGui(@NotNull NexusHub instance) {
        super(instance, Component.text("Menu Principal"), 9 * 6, false);
    }

    @Override
    public void onOpen(@NotNull Player player) {
        InventoryUtils.fillSlots(inventory, GAMES_SLOTS, List.of(Game.values()), game -> {
            final ItemStack gameItem = new ItemBuilder(game.getItemBuilder().build())
                    .displayName(game.getDisplayName().decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .flag(ObjectUtils.requireNonNullElse(game.getFlag(), Component.empty()))
                    .addTag(Component.text(game.getType(), NamedTextColor.DARK_GRAY))
                    .lore(MessageUtils.formatLore(game.getDescription(), 6).stream().map(Component::text).collect(Collectors.toList()))
                    .addProperty(Component.text("Version"), Component.text(game.getVersion(), NamedTextColor.AQUA))
                    .addAction(ItemBuilder.ANY_CLICK, Component.text("Rejoindre le jeu"))
                    .addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                    .build();

            return InteractionsBinder.bind(gameItem, (InventoryClickCallback) (targetPlayer, inventory, itemStack, action, clickType, slot) -> {
                NexusAPI.getInstance().getServersManager().connectGame(targetPlayer, game);
            });
        });

        inventory.setItem(19, InteractionsBinder.bind(new ItemBuilder(Material.ARMOR_STAND).displayName(Component.text("Revenir au spawn", NamedTextColor.GREEN, TextDecoration.BOLD)).build(), (InventoryClickCallback) (targetPlayer, inventory, itemStack, action, clickType, slot) -> instance.getSpawnManager().teleport(targetPlayer)));
        inventory.setItem(28, InteractionsBinder.bind(new ItemBuilder(Material.FEATHER).displayName(Component.text("Parcours", NamedTextColor.YELLOW, TextDecoration.BOLD)).addAction(ItemBuilder.ANY_CLICK, Component.text("Accédez aux parcours")).build(), (InventoryClickCallback) (targetPlayer, inventory, itemStack, action, clickType, slot) -> GuiManager.openGui(targetPlayer, new JumpsGui(instance, this))));
    }

    @Override
    public void onClose(@NotNull Player player) {

    }
}
