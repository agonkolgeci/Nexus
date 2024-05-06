package com.agonkolgeci.nexus;

import com.agonkolgeci.nexus.core.holograms.HologramsManager;
import com.agonkolgeci.nexus.core.players.PlayersManager;
import com.agonkolgeci.nexus.core.servers.ServersManager;
import com.agonkolgeci.nexus.core.stylizer.StylizerManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public final class NexusAPI extends AbstractPlugin {

    @Getter private static NexusAPI instance;

    @NotNull private final LuckPerms luckPerms;

    @NotNull private final PlayersManager playersManager;
    @NotNull private final ServersManager serversManager;

    @NotNull private final HologramsManager hologramsManager;
    @NotNull private final StylizerManager stylizerManager;

    public NexusAPI(@NotNull JavaPlugin plugin) {
        super(plugin);

        luckPerms = server.getServicesManager().load(LuckPerms.class);

        playersManager = new PlayersManager(this);
        serversManager = new ServersManager(this);

        hologramsManager = new HologramsManager(this);
        stylizerManager = new StylizerManager(this);
    }

    @Override
    public void load() throws Exception {
        instance = this;

        playersManager.load();
        serversManager.load();

        hologramsManager.load();
        stylizerManager.load();
    }

    @Override
    public void unload() {
        super.unload();

        playersManager.unload();
        serversManager.unload();

        hologramsManager.unload();
        stylizerManager.unload();
    }

}
