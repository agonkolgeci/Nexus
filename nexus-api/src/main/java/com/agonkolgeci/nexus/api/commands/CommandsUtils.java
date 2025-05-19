package com.agonkolgeci.nexus.api.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandsUtils {

    @NotNull
    public static List<String> retrieveAutoCompletions(@NotNull String argument, @NotNull Collection<String> completions) {
        return completions.stream().filter(completion -> completion.toLowerCase().startsWith(argument.toLowerCase(Locale.ROOT))).sorted(Comparator.comparing(String::toLowerCase)).collect(Collectors.toList());
    }

    @NotNull
    public static List<String> retrieveAutoCompletions(@NotNull String argument, @NotNull String... completions) {
        return retrieveAutoCompletions(argument, Arrays.asList(completions));
    }

    @NotNull
    public static List<String> retrievePlayersCompletions(@NotNull String argument, boolean online) {
        return retrieveAutoCompletions(argument, (online ? Bukkit.getOnlinePlayers() : Arrays.stream(Bukkit.getOfflinePlayers()).collect(Collectors.toList())).stream().map(OfflinePlayer::getName).collect(Collectors.toList()));
    }

    @NotNull
    public static Player retrieveTargetPlayerFromArgs(@NotNull CommandSender sender, @NotNull String[] args, int index) {
        if(args.length >= index+1) {
            @Nullable Player targetPlayer = Bukkit.getPlayer(args[index]);
            if(targetPlayer == null) throw new IllegalArgumentException("Le joueur spécifié n'est pas connécté sur le serveur !");

            return targetPlayer;
        }

        if(sender instanceof Player) {
            return (Player) sender;
        }

        throw new IllegalArgumentException("Désolé, mais uniquement les joueurs peuvent faire ceci.");
    }

}
