package com.agonkolgeci.nexus_hub.core.players;

import com.agonkolgeci.nexus_api.common.players.PlayerCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HubPlayer extends PlayerCache<HubPlayersController> {

    public HubPlayer(@NotNull HubPlayersController module, @NotNull Player player) {
        super(module, player);
    }

    @Override
    protected void onReady() {
        module.getAll().sendMessage(Component.text(bukkitPlayer.getDisplayName()).appendSpace().append(Component.text("vient de rejoindre le Hub !", NamedTextColor.GREEN)));
    }

    @Override
    protected void onLogout() {
        module.getAll().sendMessage(Component.text(bukkitPlayer.getDisplayName()).appendSpace().append(Component.text("vient de quitter le Hub !", NamedTextColor.RED)));
    }
}
