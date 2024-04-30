package com.agonkolgeci.nexus_api.core.players;

import com.agonkolgeci.nexus_api.NexusAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NexusPlayersController extends com.agonkolgeci.nexus_api.common.players.PlayersController<NexusAPI, NexusPlayer> {

    public NexusPlayersController(@NotNull NexusAPI instance) {
        super(instance);
    }

    @Override
    protected @NotNull NexusPlayer createUserCache(@NotNull Player player) {
        return new NexusPlayer(this, player);
    }
}
