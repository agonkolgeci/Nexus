package com.agonkolgeci.nexus_hub.core.players;

import com.agonkolgeci.nexus.api.players.AbstractPlayersManager;
import com.agonkolgeci.nexus_hub.NexusHub;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class HubPlayersManager extends AbstractPlayersManager<NexusHub, HubPlayer> {

    public HubPlayersManager(@NotNull NexusHub instance) {
        super(instance);
    }

    @Override
    protected @NotNull HubPlayer createPlayerCache(@NotNull Player player) {
        return new HubPlayer(instance, player);
    }

}
