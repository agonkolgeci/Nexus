package com.agonkolgeci.nexus_hub.core.gui.main;

import com.agonkolgeci.nexus.api.gui.AbstractGui;
import com.agonkolgeci.nexus.utils.inventory.InventoryUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuiJumps extends AbstractGui<NexusHub> {

    public static final int[] JUMPS_SLOTS = new int[] {11,12,13,14,15};

    public GuiJumps(@NotNull NexusHub instance, @NotNull AbstractGui<NexusHub> parent) {
        super(instance, Component.text("Parcours"), 9 * 3, false, parent);
    }

    @Override
    public void update(@NotNull Player player) {
        this.handleBackward(instance.getGuiManager(), size - 1);

        InventoryUtils.fillSlots(inventory, JUMPS_SLOTS, instance.getJumpsManager().getJumps(), jumpManager -> {
            return handleInteraction(new ItemBuilder(jumpManager.getMaterial())
                    .displayName(jumpManager.getDisplayName())
                    .addProperty(Component.text("Difficulté"), jumpManager.getDifficulty())
                    .addProperty(Component.text("Vitesse"), jumpManager.getSpeed())
                    .addProperty(Component.text("Temps estimée"), jumpManager.getEstimatedTime())
                    .addAction(ItemBuilder.ANY_CLICK, Component.text("Se téléporter au parcours"))
                    .build(), (targetPlayer, itemStack, action, clickType, slot) -> targetPlayer.teleport(jumpManager.getLobby()));
        });
    }
}
