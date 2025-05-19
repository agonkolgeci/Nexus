package com.agonkolgeci.nexus_hub.core.spawn;

import com.agonkolgeci.nexus.api.commands.CommandAdapter;
import com.agonkolgeci.nexus.api.commands.exceptions.IllegalCommandExecutorException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SpawnCommand implements  CommandAdapter {

    @NotNull private final SpawnManager spawnManager;

    public SpawnCommand(@NotNull SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommandComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof @NotNull Player player)) throw new IllegalCommandExecutorException();

        spawnManager.teleport(player);

        return true;
    }

    @Override
    public @Nullable List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
