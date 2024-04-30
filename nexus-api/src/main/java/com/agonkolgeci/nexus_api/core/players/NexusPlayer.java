package com.agonkolgeci.nexus_api.core.players;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NexusPlayer extends com.agonkolgeci.nexus_api.common.players.PlayerCache<NexusPlayersController> {

    public NexusPlayer(@NotNull NexusPlayersController module, @NotNull Player player) {
        super(module, player);
    }

    @Override
    protected void onReady() {

    }

    @Override
    protected void onLogout() {

    }
}
