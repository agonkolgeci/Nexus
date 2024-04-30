package com.agonkolgeci.nexus_hub.core.jumps.addons;

import com.agonkolgeci.nexus_hub.core.jumps.JumpCache;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsController;
import com.agonkolgeci.nexus_api.NexusAPI;
import com.agonkolgeci.nexus_api.common.config.ConfigSection;
import com.agonkolgeci.nexus_api.common.config.ConfigUtils;
import com.agonkolgeci.nexus_api.core.holograms.Hologram;
import com.agonkolgeci.nexus_api.plugin.PluginAddon;
import com.agonkolgeci.nexus_api.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus_api.utils.render.MessageUtils;
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
public class JumpLeaderboard extends PluginAddon<JumpsController> {

    @NotNull private final JumpCache jumpCache;
    @NotNull private final ConfigSection configuration;

    @NotNull private final Location location;
    @NotNull private final Component title;
    @NotNull private final Hologram.Direction direction;

    private final int limit;

    @NotNull private final Hologram hologram;

    public JumpLeaderboard(@NotNull JumpsController module, @NotNull JumpCache jumpCache, @NotNull ConfigSection configuration) {
        super(module);

        this.jumpCache = jumpCache;
        this.configuration = configuration;

        this.location = ConfigUtils.retrieveLocation(jumpCache.getWorld(), configuration.of("location"));
        this.title = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("title"));
        this.direction = ObjectUtils.fetchObject(Hologram.Direction.class, configuration.get("direction"), Hologram.Direction.DOWN);

        this.limit = configuration.require("limit");

        this.hologram = NexusAPI.getInstance().getHologramsController().create(location, title, direction);
    }

    public void update() {
        hologram.clearLines();

        @NotNull final LinkedHashMap<OfflinePlayer, Integer> records = jumpCache.retrieveRecords(limit);
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
