package com.agonkolgeci.nexus_proxy.core.player;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.agonkolgeci.nexus_proxy.common.player.PPlayersController;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class PlayersController extends PPlayersController<PlayerCache> {

    public static final Component TAB_HEADER = Component.text()
            .appendNewline()
            .append(Component.text("Nexus d'Agon", NamedTextColor.WHITE, TextDecoration.BOLD)).appendNewline()
            .append(Component.text("Serveur Mini-Jeux", NamedTextColor.YELLOW)).appendNewline()
            .build();

    public static final Component TAB_FOOTER = Component.text()
            .appendNewline()
            .append(Component.text("Ici, tu peux retrouver tous les mini-jeux que")).appendNewline()
            .append(Component.text("j'ai pu (re)développer durant plusieurs années !")).appendNewline()
            .appendNewline()
            .append(Component.text("nexus.agonkolgeci.com", NamedTextColor.GOLD, TextDecoration.BOLD)).appendNewline()
            .colorIfAbsent(NamedTextColor.WHITE)
            .build();

    public PlayersController(@NotNull NexusProxy instance) {
        super(instance);
    }

    @Override
    protected @NotNull PlayerCache createPlayerCache(@NotNull Player player) {
        return new PlayerCache(this, player);
    }
}
