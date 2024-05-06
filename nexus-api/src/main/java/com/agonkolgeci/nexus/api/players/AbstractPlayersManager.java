package com.agonkolgeci.nexus.api.players;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.players.events.PlayerLogoutEvent;
import com.agonkolgeci.nexus.api.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractPlayersManager<I extends AbstractPlugin, P extends AbstractPlayerCache<I>> extends PluginManager<I> implements PluginAdapter, Listener {

    @NotNull protected final Map<Player, P> players;

    public AbstractPlayersManager(@NotNull I instance) {
        super(instance);

        this.players = new HashMap<>();
    }

    @Override
    public void load() throws Exception {
        instance.getServer().getOnlinePlayers().forEach(this::initPlayer);
    }

    @Override
    public void unload() {

    }

    protected void initPlayer(@NotNull Player player) {
        @NotNull final AbstractPlayerCache<I> playerCache = this.retrievePlayerCache(player);

        playerCache.onReady();
        instance.getEventsManager().callEvent(new PlayerReadyEvent(player));
    }

    protected void removePlayer(@NotNull Player player) {
        @NotNull final AbstractPlayerCache<I> playerCache = this.retrievePlayerCache(player);

        playerCache.onLogout();
        instance.getEventsManager().callEvent(new PlayerLogoutEvent(player));
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        event.setJoinMessage(null);

        this.initPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        event.setQuitMessage(null);

        this.removePlayer(event.getPlayer());
    }

    @NotNull protected abstract P createPlayerCache(@NotNull Player player);

    @NotNull
    public P retrievePlayerCache(@NotNull Player player) {
        return ObjectUtils.retrieveObjectOrElseGet(players, player, () -> createPlayerCache(player));
    }

}
