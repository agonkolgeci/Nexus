package com.agonkolgeci.nexus;

import com.agonkolgeci.nexus.core.messaging.MessagingManager;
import com.agonkolgeci.nexus.core.players.PlayersManager;
import com.agonkolgeci.nexus.core.servers.ServersManager;
import com.agonkolgeci.nexus.core.stylizer.StylizerManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public final class NexusAPI extends AbstractPlugin {

    @Getter private static NexusAPI instance;

    @NotNull private final LuckPerms luckPerms;

    @NotNull private final PlayersManager playersManager;
    @NotNull private final ServersManager serversManager;
    @NotNull private final MessagingManager messagingManager;

    @NotNull private final StylizerManager stylizerManager;

    public NexusAPI(@NotNull JavaPlugin plugin) {
        super(plugin);

        this.luckPerms = Objects.requireNonNull(server.getServicesManager().getRegistration(LuckPerms.class), "LuckPerms is required.").getProvider();

        this.messagingManager = new MessagingManager(this);
        this.playersManager = new PlayersManager(this);
        this.serversManager = new ServersManager(this);

        this.stylizerManager = new StylizerManager(this);
    }

    @Override
    public void load() throws Exception {
        instance = this;

        playersManager.load();
        serversManager.load();
        messagingManager.load();

        stylizerManager.load();
    }

    @Override
    public void unload() {
        playersManager.unload();
        serversManager.unload();

        stylizerManager.unload();
    }

}
