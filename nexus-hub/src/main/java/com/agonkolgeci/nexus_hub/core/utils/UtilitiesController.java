package com.agonkolgeci.nexus_hub.core.utils;

import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import com.agonkolgeci.nexus_api.utils.render.MessageUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

@Getter
public class UtilitiesController extends PluginModule<NexusHub> implements PluginController, MessageUtils.Stylizable {

    public UtilitiesController(@NotNull NexusHub instance) {
        super(instance);
    }

    @Override
    public @NotNull Component getPrefix() {
        return Component.text("Utilitaires", NamedTextColor.GOLD);
    }

    @Override
    public void load() {
        instance.getCommandsController().registerCommandAdapter("speed", new SpeedCommand(this));
    }

    @Override
    public void unload() {

    }
}
