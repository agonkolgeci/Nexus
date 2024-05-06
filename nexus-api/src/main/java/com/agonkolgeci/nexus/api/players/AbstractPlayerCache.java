package com.agonkolgeci.nexus.api.players;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public abstract class AbstractPlayerCache<I extends AbstractPlugin> extends PluginManager<I> {

    @NotNull protected final Player player;

    @NotNull protected final UUID uniqueId;

    @NotNull protected final String username;
    @NotNull protected final Component displayName;

    @NotNull protected final Audience audience;

    public AbstractPlayerCache(@NotNull I instance, @NotNull Player player) {
        super(instance);

        this.player = player;

        this.uniqueId = player.getUniqueId();

        this.username = player.getName();
        this.displayName = LegacyComponentSerializer.legacySection().deserialize(player.getDisplayName());

        this.audience = instance.getAdventure().player(player);
    }

    protected abstract void onReady();
    protected abstract void onLogout();

}
