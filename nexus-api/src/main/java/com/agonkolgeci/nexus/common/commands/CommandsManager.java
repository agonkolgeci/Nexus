package com.agonkolgeci.nexus.common.commands;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.plugin.PluginManager;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CommandsManager extends AbstractAddon<AbstractPlugin> {

    public CommandsManager(@NotNull AbstractPlugin module) {
        super(module);
    }

    @NotNull
    public <C extends CommandAdapter> C registerCommandAdapter(@NotNull String commandName, @NotNull CommandAdapter commandAdapter) {
        @Nullable final PluginCommand pluginCommand = module.getPlugin().getCommand(commandName);
        if(pluginCommand == null) throw new IllegalStateException(String.format("Cannot retrieve command: %s", commandName));

        pluginCommand.setExecutor(commandAdapter);
        pluginCommand.setTabCompleter(commandAdapter);

        return (C) commandAdapter;
    }

}
