package com.agonkolgeci.nexus_api.common.players;

import com.agonkolgeci.nexus_api.plugin.PluginAddon;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public abstract class PlayerCache<C extends PlayersController<?, ?>> extends PluginAddon<C> {

    @NotNull protected final Player bukkitPlayer;

    @NotNull protected final UUID uniqueId;

    @NotNull protected final String username;
    @NotNull protected final Component displayName;

    @NotNull protected final Audience audience;

    public PlayerCache(@NotNull C module, @NotNull Player bukkitPlayer) {
        super(module);

        this.bukkitPlayer = bukkitPlayer;

        this.uniqueId = bukkitPlayer.getUniqueId();

        this.username = bukkitPlayer.getName();
        this.displayName = LegacyComponentSerializer.legacySection().deserialize(bukkitPlayer.getDisplayName());

        this.audience = module.getInstance().getAdventure().player(bukkitPlayer);
    }

    protected abstract void onReady();
    protected abstract void onLogout();

}
