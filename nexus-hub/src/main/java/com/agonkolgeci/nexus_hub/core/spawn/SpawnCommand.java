package com.agonkolgeci.nexus_hub.core.spawn;

import com.agonkolgeci.nexus_api.common.commands.CommandAdapter;
import com.agonkolgeci.nexus_api.common.commands.exceptions.IllegalCommandExecutorException;
import com.agonkolgeci.nexus_api.plugin.PluginAddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SpawnCommand extends PluginAddon<SpawnController> implements  CommandAdapter {

    public SpawnCommand(@NotNull SpawnController module) {
        super(module);
    }

    @Override
    public boolean onCommandComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) throw new IllegalCommandExecutorException();

        @NotNull final Player player = (Player) sender;
        player.teleport(module.getCenter());

        return true;
    }

    @Override
    public @Nullable List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
