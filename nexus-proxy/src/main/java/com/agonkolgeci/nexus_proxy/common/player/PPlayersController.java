package com.agonkolgeci.nexus_proxy.common.player;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.agonkolgeci.nexus_proxy.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_proxy.plugin.PluginController;
import com.agonkolgeci.nexus_proxy.plugin.PluginModule;
import com.agonkolgeci.nexus_proxy.utils.objects.ObjectUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PPlayersController<P extends PPlayerCache<?>> extends PluginModule implements PluginController, ListenerAdapter {

    @NotNull protected final Map<Player, P> players;

    public PPlayersController(@NotNull NexusProxy instance) {
        super(instance);

        this.players = new HashMap<>();
    }

    @Override
    public void load() {
        proxy.getAllPlayers().forEach(this::loadPlayer);

        instance.getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        proxy.getAllPlayers().forEach(this::unloadPlayer);

        instance.getEventsController().unregisterEventAdapter(this);
    }

    protected void loadPlayer(@NotNull Player player) {
        createPlayerCache(player).onLogin();
    }

    protected void unloadPlayer(@NotNull Player player) {
        retrievePlayerCache(player).onLogout();
    }

    @Subscribe
    public void onPlayerLogin(@NotNull ServerConnectedEvent event) {
        loadPlayer(event.getPlayer());
    }

    @Subscribe
    public void onPlayerLogout(@NotNull DisconnectEvent event) {
        unloadPlayer(event.getPlayer());
    }

    @NotNull protected abstract P createPlayerCache(@NotNull Player player);

    @NotNull
    protected P retrievePlayerCache(@NotNull Player player) {
        return ObjectUtils.retrieveObjectOrElseGet(players, player, () -> createPlayerCache(player));
    }

}
