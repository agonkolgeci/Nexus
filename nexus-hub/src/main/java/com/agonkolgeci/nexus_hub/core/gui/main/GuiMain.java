package com.agonkolgeci.nexus_hub.core.gui.main;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.gui.AbstractGui;
import com.agonkolgeci.nexus.core.servers.Game;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.render.InventoryUtils;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiMain extends AbstractGui<NexusHub> {

    public static final int[] GAMES_SLOTS = new int[] {22,23,24,25,31,32,33,34};

    public GuiMain(@NotNull NexusHub instance) {
        super(instance, Component.text("Menu Principal"), 9 * 6, false);
    }

    @Override
    public void update(@NotNull Player player) {
        InventoryUtils.fillSlots(inventory, GAMES_SLOTS, Arrays.asList(Game.values()), (game) -> {
            return handleInteraction(new ItemBuilder<>(game.getItemBuilder().toItemStack())
                    .displayName(game.getDisplayName().decorationIfAbsent(TextDecoration.BOLD, TextDecoration.State.TRUE))
                    .flag(ObjectUtils.requireNonNullElse(game.getFlag(), Component.empty()))
                    .addTag(Component.text(game.getType(), NamedTextColor.DARK_GRAY))
                    .lore(MessageUtils.formatLore(game.getDescription(), 6).stream().map(Component::text).collect(Collectors.toList()))
                    .addLore(Component.empty())
                    .addProperty(Component.text("Version"), Component.text(game.getVersion(), NamedTextColor.AQUA))
                    .addAction(ItemBuilder.ANY_CLICK, Component.text("Rejoindre le jeu"))
                    .hideAttributes()
                    .toItemStack(), (targetPlayer, itemStack, action, clickType, slot) -> NexusAPI.getInstance().getServersManager().connectGame(targetPlayer, game));
        });

        inventory.setItem(19, handleInteraction(new ItemBuilder<>(Material.ARMOR_STAND).displayName(Component.text("Revenir au spawn", NamedTextColor.GREEN, TextDecoration.BOLD)).toItemStack(), (targetPlayer, itemStack, action, clickType, slot) -> instance.getSpawnManager().teleportPlayer(targetPlayer)));
        inventory.setItem(28, handleInteraction(new ItemBuilder<>(Material.FEATHER).displayName(Component.text("Parcours", NamedTextColor.YELLOW, TextDecoration.BOLD)).addAction(ItemBuilder.ANY_CLICK, Component.text("Accédez aux parcours")).toItemStack(), (targetPlayer, itemStack, action, clickType, slot) -> instance.getGuiManager().openGui(targetPlayer, new GuiJumps(instance, this))));
    }
}
