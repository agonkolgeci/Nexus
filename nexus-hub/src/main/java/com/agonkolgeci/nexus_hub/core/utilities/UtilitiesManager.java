package com.agonkolgeci.nexus_hub.core.utilities;

import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.ui.TextMessaging;
import com.agonkolgeci.nexus_hub.NexusHub;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

@Getter
public class UtilitiesManager extends PluginManager<NexusHub> implements PluginAdapter {

    public static final TextMessaging MESSAGING = new TextMessaging("Utilitaires", NamedTextColor.GOLD);

    public UtilitiesManager(@NotNull NexusHub instance) {
        super(instance);
    }

    @Override
    public void load() throws Exception {
        instance.getCommandsManager().registerAdapter("speed", new SpeedCommand(this));
    }

    @Override
    public void unload() {

    }
}
