package com.agonkolgeci.nexus_proxy.core.player;

import com.agonkolgeci.nexus_proxy.common.player.PPlayerCache;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCache extends PPlayerCache<PlayersController> {

    public PlayerCache(@NotNull PlayersController module, @NotNull Player player) {
        super(module, player);
    }

    @Override
    protected void onLogin() {
        player.sendPlayerListHeaderAndFooter(PlayersController.TAB_HEADER, PlayersController.TAB_FOOTER);
    }

    @Override
    protected void onLogout() {

    }
}
