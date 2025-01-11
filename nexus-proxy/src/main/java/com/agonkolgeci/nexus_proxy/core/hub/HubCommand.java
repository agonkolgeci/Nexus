package com.agonkolgeci.nexus_proxy.core.hub;

import com.agonkolgeci.nexus_proxy.common.commands.CommandAdapter;
import com.agonkolgeci.nexus_proxy.common.commands.exceptions.IllegalCommandExecutorException;
import com.agonkolgeci.nexus_proxy.plugin.PluginAddon;
import com.agonkolgeci.nexus_proxy.utils.minecraft.audience.MessageUtils;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HubCommand extends PluginAddon<HubsController> implements CommandAdapter {

    public HubCommand(@NotNull HubsController module) {
        super(module);
    }

    @Override
    public @NotNull CommandMeta getCommandMeta(@NotNull CommandManager commandManager) {
        return commandManager.metaBuilder("hub").aliases("lobby").build();
    }

    @Override
    public void onCommandComplete(@NotNull Invocation invocation) {
        @NotNull final CommandSource source = invocation.source();
        if(!(source instanceof @NotNull final Player player)) {
            MessageUtils.sendError(source, new IllegalCommandExecutorException());

            return;
        }

        module.sendHub(player);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull Invocation invocation) {
        return List.of();
    }
}
