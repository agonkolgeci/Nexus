package com.agonkolgeci.nexus_api.common.commands;

import com.agonkolgeci.nexus_api.NexusPlugin;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CommandsController extends PluginModule<NexusPlugin> {

    public CommandsController(@NotNull NexusPlugin instance) {
        super(instance);
    }

    @NotNull
    public <C extends CommandAdapter> C registerCommandAdapter(@NotNull String commandName, @NotNull CommandAdapter commandAdapter) {
        @Nullable final PluginCommand pluginCommand = plugin.getCommand(commandName);
        if(pluginCommand == null) throw new IllegalStateException(String.format("Cannot retrieve command: %s", commandName));

        pluginCommand.setExecutor(commandAdapter);
        pluginCommand.setTabCompleter(commandAdapter);

        return (C) commandAdapter;
    }

}
