package com.agonkolgeci.nexus.api.commands;

import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface CommandAdapter extends CommandExecutor, TabCompleter {

    default boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            return onCommandComplete(sender, command, label, args);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());

            return true;
        } catch (Exception exception) {
            exception.printStackTrace();

            return false;
        }
    }

    boolean onCommandComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    @Nullable
    default List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            return ObjectUtils.requireNonNullElse(onCommandTabComplete(sender, command, label, args), Collections.emptyList());
        } catch (IllegalStateException | IllegalArgumentException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return Collections.emptyList();
    }

    @Nullable List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

}
