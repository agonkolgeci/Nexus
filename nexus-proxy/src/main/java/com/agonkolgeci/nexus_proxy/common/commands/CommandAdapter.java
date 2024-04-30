package com.agonkolgeci.nexus_proxy.common.commands;

import com.agonkolgeci.nexus_proxy.utils.minecraft.audience.MessageUtils;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface CommandAdapter extends SimpleCommand {

    @NotNull public abstract CommandMeta getCommandMeta(@NotNull CommandManager commandManager);

    @Override
    default void execute(@NotNull Invocation invocation) {
        try {
            this.onCommandComplete(invocation);
        } catch (RuntimeException exception) {
            MessageUtils.sendError(invocation.source(), exception);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public abstract void onCommandComplete(@NotNull Invocation invocation);

    @Override
    default List<String> suggest(@NotNull Invocation invocation) {
        try {
            return this.onTabComplete(invocation);
        } catch (RuntimeException exception) {
            MessageUtils.sendError(invocation.source(), exception);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return List.of();
    }

    @NotNull public abstract List<String> onTabComplete(@NotNull Invocation invocation);

}
