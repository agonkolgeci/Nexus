package com.agonkolgeci.nexus_api;

import com.agonkolgeci.nexus_api.core.holograms.HologramsController;
import com.agonkolgeci.nexus_api.core.interactions.InteractionsController;
import com.agonkolgeci.nexus_api.core.players.NexusPlayersController;
import com.agonkolgeci.nexus_api.core.stylizer.StylizerController;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public final class NexusAPI extends NexusPlugin {

    public static NexusAPI getInstance() {
        return (NexusAPI) instance;
    }

    @NotNull private final ProtocolManager protocolManager;
    @NotNull private final LuckPerms luckPerms;

    @NotNull private final InteractionsController interactionsController;
    @NotNull private final HologramsController hologramsController;

    @NotNull private final StylizerController stylizerController;

    @NotNull private final NexusPlayersController playersController;

    public NexusAPI(@NotNull JavaPlugin plugin) {
        super(plugin);

        protocolManager = ProtocolLibrary.getProtocolManager();
        luckPerms = servicesManager.load(LuckPerms.class);

        interactionsController = new InteractionsController(this);
        hologramsController = new HologramsController(this);

        stylizerController = new StylizerController(this);

        playersController = new NexusPlayersController(this);
    }

    @Override
    public void load() throws Exception {
        super.load();

        interactionsController.load();
        hologramsController.load();

        stylizerController.load();

        playersController.load();
    }

    @Override
    public void unload() {
        super.unload();

        playersController.unload();

        stylizerController.unload();

        hologramsController.unload();
        interactionsController.unload();
    }

}
