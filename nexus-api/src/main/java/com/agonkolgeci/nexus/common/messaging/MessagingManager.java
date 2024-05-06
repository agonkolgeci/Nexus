package com.agonkolgeci.nexus.common.messaging;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;

@Getter
public class MessagingManager extends PluginManager<AbstractPlugin> implements PluginAdapter {

    @NotNull private final Messenger messenger;

    public MessagingManager(@NotNull AbstractPlugin instance) {
        super(instance);

        this.messenger = instance.getServer().getMessenger();
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() {

    }

}
