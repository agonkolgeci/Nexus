package com.agonkolgeci.nexus_proxy.common.player;

import com.agonkolgeci.nexus_proxy.plugin.PluginAddon;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PPlayerCache<C extends PPlayersController<?>> extends PluginAddon<C> {

    @NotNull protected final Player player;

    public PPlayerCache(@NotNull C module, @NotNull Player player) {
        super(module);

        this.player = player;
    }

    protected abstract void onLogin();
    protected abstract void onLogout();

}
