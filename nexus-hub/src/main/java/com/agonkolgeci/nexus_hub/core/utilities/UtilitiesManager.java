package com.agonkolgeci.nexus_hub.core.utilities;

import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

@Getter
public class UtilitiesManager extends PluginManager<NexusHub> implements PluginAdapter, MessageUtils.Stylizable {

    public UtilitiesManager(@NotNull NexusHub instance) {
        super(instance);
    }

    @Override
    public @NotNull Component getPrefix() {
        return Component.text("Utilitaires", NamedTextColor.GOLD);
    }

    @Override
    public void load() throws Exception {
        instance.getCommandsManager().registerCommandAdapter("speed", new SpeedCommand(this));
    }

    @Override
    public void unload() {

    }
}
