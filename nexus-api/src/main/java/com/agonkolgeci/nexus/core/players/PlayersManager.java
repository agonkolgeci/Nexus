package com.agonkolgeci.nexus.core.players;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.players.AbstractPlayersManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayersManager extends AbstractPlayersManager<NexusAPI, NexusPlayer> {

    public PlayersManager(@NotNull NexusAPI instance) {
        super(instance);
    }

    @Override
    protected @NotNull NexusPlayer createPlayerCache(@NotNull Player player) {
        return new NexusPlayer(instance, player);
    }
}
