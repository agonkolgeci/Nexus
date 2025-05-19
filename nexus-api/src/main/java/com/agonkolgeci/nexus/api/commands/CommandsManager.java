package com.agonkolgeci.nexus.api.commands;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandsManager extends PluginManager<AbstractPlugin> {

    public CommandsManager(@NotNull AbstractPlugin instance) {
        super(instance);
    }

    public void registerAdapter(@NotNull String commandName, @NotNull CommandAdapter commandAdapter) {
        @Nullable final PluginCommand pluginCommand = instance.getPlugin().getCommand(commandName);
        if(pluginCommand == null) throw new IllegalStateException(String.format("Cannot retrieve command: %s", commandName));

        pluginCommand.setExecutor(commandAdapter);
        pluginCommand.setTabCompleter(commandAdapter);
    }

}
