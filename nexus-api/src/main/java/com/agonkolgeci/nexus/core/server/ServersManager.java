package com.agonkolgeci.nexus.core.server;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.core.messaging.PluginChannelType;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
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
        instance.getMessagingManager().send(player, PluginChannelType.BUNGEE_CORD, out -> {
            out.writeUTF("ConnectOther");
            out.writeUTF(player.getName());
            out.writeUTF(game.getId());
        });
    }
}
