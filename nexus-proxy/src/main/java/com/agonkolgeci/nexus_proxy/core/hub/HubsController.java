package com.agonkolgeci.nexus_proxy.core.hub;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.agonkolgeci.nexus_proxy.core.hub.exceptions.AlreadyInHubException;
import com.agonkolgeci.nexus_proxy.core.hub.exceptions.NoneHubAvailableException;
import com.agonkolgeci.nexus_proxy.plugin.PluginController;
import com.agonkolgeci.nexus_proxy.plugin.PluginModule;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

@Getter
public class HubsController extends PluginModule implements PluginController<HubsController> {

    public static final String SERVER_NAME_PATTERN = "(?i)(hub|lobby)";

    @NotNull private final List<RegisteredServer> servers;

    @NotNull private final HubCommand command;

    public HubsController(@NotNull NexusProxy instance) {
        super(instance);

        this.servers =  proxy.getAllServers().stream().filter(this::isHub).toList();

        this.command = new HubCommand(this);
    }

    @Override
    public void load() {
        if(servers.isEmpty()) {
            logger.warn("There are no hubs available.");
        }

        instance.getCommandsController().registerCommandAdapter(command);
    }

    @Override
    public void unload() {
        instance.getCommandsController().unregisterCommandAdapter(command);
    }

    @Nullable
    public RegisteredServer retrieveHub() {
        return servers.stream().min(Comparator.comparingInt(server -> server.getPlayersConnected().size())).filter(server -> server.ping().thenApply(r -> true).exceptionally(e -> false).join()).orElse(null);
    }

    public boolean isHub(@NotNull RegisteredServer server) {
        return server.getServerInfo().getName().matches(SERVER_NAME_PATTERN);
    }

    public boolean isInHub(@NotNull Player player) {
        @Nullable final ServerConnection serverConnection = player.getCurrentServer().orElse(null);
        return serverConnection != null && isHub(serverConnection.getServer());
    }

    public void sendHub(@NotNull Player player) {
        if(isInHub(player)) throw new AlreadyInHubException();

        @Nullable final RegisteredServer targetServer = retrieveHub();
        if(targetServer == null) throw new NoneHubAvailableException();

        player.createConnectionRequest(targetServer).connect().join();
    }
}
