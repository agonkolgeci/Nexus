package com.agonkolgeci.nexus_hub.core.jumps;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.api.config.ConfigUtils;
import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLeaderboard;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLocation;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class JumpManager extends PluginManager<NexusHub> implements PluginAdapter, ListenerAdapter {

    @NotNull private final JumpsManager jumpsManager;

    @NotNull private final ConfigSection configuration;

    @NotNull private final String name;

    @NotNull private final Component displayName;
    @NotNull private final Material material;
    @NotNull private final Component difficulty;
    @NotNull private final Component speed;
    @NotNull private final Component estimatedTime;

    @NotNull private final World world;

    private final float walkSpeed;

    @NotNull private final Location lobby;

    @NotNull private final JumpLocation start;
    @NotNull private final JumpLocation finish;
    @NotNull private final Map<Integer, JumpLocation> checkpoints;

    @NotNull private final JumpLeaderboard leaderboard;

    public JumpManager(@NotNull NexusHub instance, @NotNull JumpsManager jumpsManager, @NotNull String name, @NotNull ConfigSection configuration) {
        super(instance);

        this.jumpsManager = jumpsManager;

        this.configuration = configuration;

        this.name = name;

        this.displayName = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("displayName"));
        this.material = ObjectUtils.fetchObject(Material.class, configuration.require("material"), Material.FEATHER);
        this.difficulty = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("difficulty"));
        this.speed = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("speed"));
        this.estimatedTime = MessageUtils.MM_SERIALIZER.deserialize(configuration.require("estimatedTime"));

        this.world = ConfigUtils.retrieveWorld(instance, configuration.of());

        this.walkSpeed = (float) ((double) configuration.require("walk-speed"));

        this.lobby = ConfigUtils.retrieveLocation(world, configuration.of("lobby"));

        this.start = JumpsManager.retrieveLocation(world, configuration.of("start"));
        this.finish = JumpsManager.retrieveLocation(world, configuration.of("finish"));
        this.checkpoints = configuration.of("checkpoints").keys((key, section) -> Integer.parseInt(key), (type, section) -> JumpsManager.retrieveLocation(world, section), false);

        this.leaderboard = new JumpLeaderboard(jumpsManager, this, configuration.of("leaderboard"));
    }

    @NotNull
    public LinkedHashMap<OfflinePlayer, Duration> retrieveRecords(int limit) {
        @NotNull final LinkedHashMap<OfflinePlayer, Duration> records = new LinkedHashMap<>();

        try {
            @NotNull final ResultSet results = instance.getDatabaseManager().executeQuery("SELECT * FROM jumps_records WHERE jump_name = ? ORDER BY time ASC LIMIT ?", name, limit);

            while (results.next()) {
                try {
                    @NotNull final UUID uuid = UUID.fromString(results.getString("player_uuid"));
                    @NotNull final OfflinePlayer offlinePlayer = instance.getServer().getOfflinePlayer(uuid);

                    final Duration time = Duration.ofMillis(results.getLong("time"));

                    records.put(offlinePlayer, time);
                } catch (IllegalArgumentException ignored) {}
            }

            instance.getDatabaseManager().closeResults(results);
        } catch (SQLException exception) {
            throw new IllegalStateException("Impossible de récupérer les records de temps.");
        }

        return records;
    }

    @Override
    public void load() {
        jumpsManager.getInstance().getEventsManager().registerAdapter(this);

        leaderboard.update();
    }

    @Override
    public void unload() {
        jumpsManager.getInstance().getEventsManager().unregisterAdapter(this);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;

        @NotNull final Player player = event.getPlayer();

        @Nullable final Block interactedBlock = event.getClickedBlock();
        if(interactedBlock == null) return;

        @NotNull final Location interactedLocation = interactedBlock.getLocation();
        @Nullable final InteractionType interactionType = InteractionType.retrieveInteractionType(this, interactedLocation);
        if(interactionType == null) return;

        @Nullable final JumpPlayer jumpPlayer = jumpsManager.getJumpPlayer(player);

        try {
            switch (interactionType) {
                case START: {
                    if(jumpPlayer != null) return;

                    jumpsManager.addPlayer(this, player);

                    break;
                }

                case CHECKPOINT: {
                    if(jumpPlayer == null) return;

                    @Nullable final Map.Entry<Integer, JumpLocation> checkpoint = checkpoints.entrySet().stream().filter(e -> e.getValue().distance(interactedLocation) == 0).findFirst().orElse(null);
                    if(checkpoint == null) return;
                    if(checkpoint == jumpPlayer.getCheckpoint()) return;

                    if(checkpoint != jumpPlayer.retrieveNextCheckpoint()) {
                        throw new IllegalStateException("Vous devez prendre les checkpoints dans l'ordre !");
                    }

                    jumpPlayer.setCheckpoint(checkpoint);

                    break;
                }

                case FINISH: {
                    if(jumpPlayer == null) return;

                    jumpPlayer.finish();

                    break;
                }
            }
        } catch (RuntimeException exception) {
            jumpsManager.removePlayer(player);

            player.sendMessage(JumpsManager.MESSAGING.error(exception));
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        @NotNull final Player player = event.getPlayer();

        jumpsManager.computeIfPresent(player, jumpPlayer -> {
            if(player.getLocation().getY() >= jumpPlayer.retrieveLatestLocation().getLowerY()) return;

            jumpPlayer.back();
        });
    }

    public enum InteractionType {

        START,
        CHECKPOINT,
        FINISH;

        @Nullable
        public static InteractionType retrieveInteractionType(@NotNull JumpManager jumpManager, @NotNull Location interactedLocation) {
            if(interactedLocation.distance(jumpManager.getStart()) == 0) {
                return START;
            } else if(interactedLocation.distance(jumpManager.getFinish()) == 0) {
                return FINISH;
            } else if(jumpManager.getCheckpoints().values().stream().anyMatch(jumpLocation -> jumpLocation.distance(interactedLocation) == 0)) {
                return CHECKPOINT;
            }

            return null;
        }

    }

}
