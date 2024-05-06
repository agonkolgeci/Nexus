package com.agonkolgeci.nexus_hub.core.jumps.components;

import com.agonkolgeci.nexus_hub.core.jumps.JumpManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.common.config.ConfigUtils;
import com.agonkolgeci.nexus.core.holograms.Hologram;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class JumpLeaderboard extends AbstractAddon<JumpsManager> {

    @NotNull private final JumpManager jumpManager;
    @NotNull private final ConfigSection configuration;

    @NotNull private final Location location;
    @NotNull private final Component title;
    @NotNull private final Hologram.Direction direction;

    private final int limit;

    @NotNull private final Hologram hologram;

    public JumpLeaderboard(@NotNull JumpsManager module, @NotNull JumpManager jumpManager, @NotNull ConfigSection configuration) {
        super(module);

        this.jumpManager = jumpManager;
        this.configuration = configuration;

        this.location = ConfigUtils.retrieveLocation(jumpManager.getWorld(), configuration.of("location"));
        this.title = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("title"));
        this.direction = ObjectUtils.fetchObject(Hologram.Direction.class, configuration.get("direction"), Hologram.Direction.DOWN);

        this.limit = configuration.require("limit");

        this.hologram = NexusAPI.getInstance().getHologramsManager().create(location, title, direction);
    }

    public void update() {
        hologram.clearLines();

        @NotNull final LinkedHashMap<OfflinePlayer, Integer> records = jumpManager.retrieveRecords(limit);
        if(!records.isEmpty()) {
            int position = 1;
            for(@NotNull final Map.Entry<OfflinePlayer, Integer> entry : records.entrySet()) {
                hologram.addLine(Component.text()
                        .append(Component.text(position+".", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        .appendSpace()
                        .append(Component.text(entry.getKey().getName(), NamedTextColor.WHITE))
                        .appendSpace()
                        .append(Component.text("-", NamedTextColor.GRAY))
                        .appendSpace()
                        .append(module.retrieveTimer(entry.getValue()))
                        .build()
                );

                position++;
            }
        } else {
            hologram.addLine(Component.text("Aucun score", NamedTextColor.GRAY));
        }
    }

}
