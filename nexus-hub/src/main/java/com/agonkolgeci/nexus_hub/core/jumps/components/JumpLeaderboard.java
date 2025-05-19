package com.agonkolgeci.nexus_hub.core.jumps.components;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.api.config.ConfigUtils;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus_hub.core.jumps.JumpManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class JumpLeaderboard {

    @NotNull private final JumpsManager jumpsManager;

    @NotNull private final JumpManager jumpManager;
    @NotNull private final ConfigSection configuration;

    @NotNull private final Location location;
    @NotNull private final String title;

    private final int limit;

    @NotNull private final Hologram hologram;

    public JumpLeaderboard(@NotNull JumpsManager jumpsManager, @NotNull JumpManager jumpManager, @NotNull ConfigSection configuration) {
        this.jumpsManager = jumpsManager;
        this.jumpManager = jumpManager;
        this.configuration = configuration;

        this.location = ConfigUtils.retrieveLocation(jumpManager.getWorld(), configuration.of("location"));
        this.title = LegacyComponentSerializer.legacyAmpersand().serialize(MessageUtils.MM_SERIALIZER.deserialize(configuration.require("title")));

        this.limit = configuration.require("limit");

        this.hologram = DHAPI.createHologram(jumpManager.getName().replaceAll("[^A-Za-z0-9]", "_"), location);
    }

    public void update() {
        @NotNull final LinkedHashMap<OfflinePlayer, Integer> records = jumpManager.retrieveRecords(limit);
        if(!records.isEmpty()) {
            @NotNull final List<String> lines = new ArrayList<>(Arrays.asList(title, ""));

            final AtomicInteger position = new AtomicInteger(1);
            records.forEach((player, time) -> {
                lines.add(LegacyComponentSerializer.legacyAmpersand().serialize(
                        Component.text()
                                .append(Component.text(position + ".", NamedTextColor.YELLOW, TextDecoration.BOLD))
                                .appendSpace()
                                .append(Component.text(Objects.requireNonNull(player.getName()), NamedTextColor.WHITE))
                                .appendSpace()
                                .append(Component.text("-", NamedTextColor.GRAY))
                                .appendSpace()
                                .append(jumpsManager.retrieveTimer(time))
                                .build()
                ));

                position.getAndIncrement();
            });

            DHAPI.setHologramLines(hologram, lines);
        } else {
            DHAPI.setHologramLines(hologram, List.of(LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("Aucun score", NamedTextColor.GRAY))));
        }
    }

}
