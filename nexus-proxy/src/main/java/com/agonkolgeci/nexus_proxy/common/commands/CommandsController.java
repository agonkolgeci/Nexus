package com.agonkolgeci.nexus_proxy.common.commands;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.agonkolgeci.nexus_proxy.plugin.PluginModule;
import com.velocitypowered.api.command.CommandManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CommandsController extends PluginModule {

    @NotNull private final CommandManager commandManager;

    public CommandsController(@NotNull NexusProxy instance) {
        super(instance);

        this.commandManager = proxy.getCommandManager();
    }

    @NotNull
    public <C extends CommandAdapter> C registerCommandAdapter(@NotNull C commandAdapter) {
        commandManager.register(commandAdapter.getCommandMeta(commandManager), commandAdapter);

        return commandAdapter;
    }

    public void unregisterCommandAdapter(@NotNull CommandAdapter commandAdapter) {
        commandManager.unregister(commandAdapter.getCommandMeta(commandManager));
    }

}
