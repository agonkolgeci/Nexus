package com.agonkolgeci.nexus.core.players;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.players.AbstractPlayerCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NexusPlayer extends AbstractPlayerCache<NexusAPI> {

    public NexusPlayer(@NotNull NexusAPI instance, @NotNull Player player) {
        super(instance, player);
    }

    @Override
    protected void onReady() {
    }

    @Override
    protected void onLogout() {

    }
}
