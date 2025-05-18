package com.agonkolgeci.nexus_hub.core.jumps.components;

import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.common.config.ConfigUtils;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class JumpLeaderboard extends AbstractAddon<JumpsManager> {

    @NotNull private final JumpManager jumpManager;
    @NotNull private final ConfigSection configuration;

    @NotNull private final Location location;
    @NotNull private final String title;

    private final int limit;

    @NotNull private final Hologram hologram;

    public JumpLeaderboard(@NotNull JumpsManager module, @NotNull JumpManager jumpManager, @NotNull ConfigSection configuration) {
        super(module);

        this.jumpManager = jumpManager;
        this.configuration = configuration;

        this.location = ConfigUtils.retrieveLocation(jumpManager.getWorld(), configuration.of("location"));
        this.title = LegacyComponentSerializer.legacyAmpersand().serialize(MessageUtils.MM_SERIALIZER.deserialize(configuration.require("title")));

        this.limit = configuration.require("limit");

        this.hologram = DHAPI.createHologram(title, location);
    }

    public void update() {
        @NotNull final LinkedHashMap<OfflinePlayer, Integer> records = jumpManager.retrieveRecords(limit);
        if(!records.isEmpty()) {
            int position = 1;
            DHAPI.setHologramLines(hologram, records.entrySet().stream().map(entry -> {
                return LegacyComponentSerializer.legacyAmpersand().serialize(
                        Component.text()
                                .append(Component.text(position+".", NamedTextColor.YELLOW, TextDecoration.BOLD))
                                .appendSpace()
                                .append(Component.text(Objects.requireNonNull(entry.getKey().getName()), NamedTextColor.WHITE))
                                .appendSpace()
                                .append(Component.text("-", NamedTextColor.GRAY))
                                .appendSpace()
                                .append(module.retrieveTimer(entry.getValue()))
                                .build()
                );
            }).collect(Collectors.toList()));
        } else {
            DHAPI.setHologramLines(hologram, List.of(LegacyComponentSerializer.legacyAmpersand().serialize(Component.text("Aucun score", NamedTextColor.GRAY))));
        }
    }

}
