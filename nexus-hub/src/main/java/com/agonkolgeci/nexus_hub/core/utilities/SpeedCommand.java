package com.agonkolgeci.nexus_hub.core.utilities;

import com.agonkolgeci.nexus.api.commands.CommandAdapter;
import com.agonkolgeci.nexus.api.commands.exceptions.IllegalCommandExecutorException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SpeedCommand implements  CommandAdapter {

    @NotNull private final UtilitiesManager utilitiesManager;

    public SpeedCommand(@NotNull UtilitiesManager utilitiesManager) {
        this.utilitiesManager = utilitiesManager;
    }

    @Override
    public boolean onCommandComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) throw new IllegalCommandExecutorException();

        if(args.length >= 1) {
            try {
                int speedTarget  = Math.min(10, Math.max(0, Integer.parseInt(args[0])));
                float speedValue = (float) speedTarget / 10;

                if(player.isFlying()) {
                    player.setFlySpeed(speedValue);
                } else {
                    player.setWalkSpeed(speedValue);
                }

                player.sendMessage(UtilitiesManager.MESSAGING.success(Component.text(String.format("Vous avez défini votre vitesse de %s à", player.isFlying() ? "vol" : "marche")).appendSpace().append(Component.text(speedTarget, NamedTextColor.YELLOW)).append(Component.text("."))));

                return true;
            } catch (NumberFormatException exception) {
                throw new IllegalStateException("Vous devez entrez un nombre valide.");
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
