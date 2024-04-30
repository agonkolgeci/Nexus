package com.agonkolgeci.nexus_api.common.players;

import com.agonkolgeci.nexus_api.NexusPlugin;
import com.agonkolgeci.nexus_api.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_api.common.players.events.PlayerLogoutEvent;
import com.agonkolgeci.nexus_api.common.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import com.agonkolgeci.nexus_api.utils.objects.ObjectUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PlayersController<I extends NexusPlugin, P extends PlayerCache<?>> extends PluginModule<I> implements PluginController, ListenerAdapter {

    @NotNull protected final Map<Player, P> users;

    public PlayersController(@NotNull I instance) {
        super(instance);

        this.users = new HashMap<>();
    }

    @Override
    public void load() {
        server.getOnlinePlayers().forEach(this::loadUser);

        instance.getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        server.getOnlinePlayers().forEach(this::unloadUser);

        instance.getEventsController().unregisterEventAdapter(this);
    }

    protected void loadUser(@NotNull Player player) {
        @NotNull final P playerCache = this.retrieveUserCache(player);

        playerCache.onReady();
        instance.getEventsController().callEvent(new PlayerReadyEvent(player));
    }

    protected void unloadUser(@NotNull Player player) {
        @NotNull final P playerCache = this.retrieveUserCache(player);

        playerCache.onLogout();
        instance.getEventsController().callEvent(new PlayerLogoutEvent(player));
    }

    @EventHandler
    public void onUserJoin(@NotNull PlayerJoinEvent event) {
        event.setJoinMessage(null);

        this.loadUser(event.getPlayer());
    }

    @EventHandler
    public void onUserQuit(@NotNull PlayerQuitEvent event) {
        event.setQuitMessage(null);

        this.unloadUser(event.getPlayer());
    }

    @NotNull protected abstract P createUserCache(@NotNull Player player);

    @NotNull
    public P retrieveUserCache(@NotNull Player player) {
        return ObjectUtils.retrieveObjectOrElseGet(users, player, () -> createUserCache(player));
    }

}
