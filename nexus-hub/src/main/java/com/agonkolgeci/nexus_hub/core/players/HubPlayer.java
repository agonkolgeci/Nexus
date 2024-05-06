package com.agonkolgeci.nexus_hub.core.players;

import com.agonkolgeci.nexus.api.players.AbstractPlayerCache;
import com.agonkolgeci.nexus_hub.NexusHub;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HubPlayer extends AbstractPlayerCache<NexusHub> {

    public HubPlayer(@NotNull NexusHub instance, @NotNull Player player) {
        super(instance, player);
    }

    @Override
    protected void onReady() {
        instance.getAll().sendMessage(Component.text(player.getDisplayName()).appendSpace().append(Component.text("vient de rejoindre le Hub !", NamedTextColor.GREEN)));
    }

    @Override
    protected void onLogout() {
        instance.getAll().sendMessage(Component.text(player.getDisplayName()).appendSpace().append(Component.text("vient de quitter le Hub !", NamedTextColor.RED)));
    }
}
