package com.agonkolgeci.nexus_hub.core.players;

import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_api.common.players.PlayersController;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class HubPlayersController extends PlayersController<NexusHub, HubPlayer> {

    public HubPlayersController(@NotNull NexusHub instance) {
        super(instance);
    }

    @Override
    protected @NotNull HubPlayer createUserCache(@NotNull Player player) {
        return new HubPlayer(this, player);
    }

}
