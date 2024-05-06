package com.agonkolgeci.nexus.core.servers;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.players.AbstractPlayerCache;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class ServersManager extends PluginManager<NexusAPI> implements PluginAdapter {

    public ServersManager(@NotNull NexusAPI instance) {
        super(instance);
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() {

    }

    public void connectGame(@NotNull Player player, @NotNull Game game) {
        // TMP CODE
        player.performCommand("server " + game.toString());
    }
}
