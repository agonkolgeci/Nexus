package com.agonkolgeci.nexus_hub.core.gui.main;

import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.core.binder.item.InventoryClickCallback;
import com.agonkolgeci.nexus.core.gui.AbstractGui;
import com.agonkolgeci.nexus.utils.inventory.InventoryUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class JumpsGui extends AbstractGui<NexusHub> {

    public static final int[] JUMPS_SLOTS = new int[] {11,12,13,14,15};

    public JumpsGui(@NotNull NexusHub instance, @NotNull AbstractGui<NexusHub> parent) {
        super(instance, parent, Component.text("Parcours"), 9 * 3, false);
    }

    @Override
    public void onOpen(@NotNull Player player) {
        this.addBackward(size - 1);

        InventoryUtils.fillSlots(inventory, JUMPS_SLOTS, instance.getJumpsManager().getJumps(), jumpManager -> {
            final ItemStack jumpItem = new ItemBuilder(jumpManager.getMaterial())
                    .displayName(jumpManager.getDisplayName())
                    .addProperty(Component.text("Difficulté"), jumpManager.getDifficulty())
                    .addProperty(Component.text("Vitesse"), jumpManager.getSpeed())
                    .addProperty(Component.text("Temps estimée"), jumpManager.getEstimatedTime())
                    .addAction(ItemBuilder.ANY_CLICK, Component.text("Se téléporter au parcours"))
                    .build();

            return InteractionsBinder.bind(jumpItem, (InventoryClickCallback) (targetPlayer, inventory, itemStack, action, clickType, slot) -> {
                targetPlayer.teleport(jumpManager.getLobby());
            });
        });
    }

    @Override
    public void onClose(@NotNull Player player) {

    }
}
