package com.agonkolgeci.nexus_hub.core.jumps;

import com.agonkolgeci.nexus_hub.core.jumps.addons.JumpLeaderboard;
import com.agonkolgeci.nexus_hub.core.jumps.addons.JumpPlayer;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLocation;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import com.agonkolgeci.nexus_api.common.config.ConfigSection;
import com.agonkolgeci.nexus_api.common.config.ConfigUtils;
import com.agonkolgeci.nexus_api.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_api.plugin.PluginAddon;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.utils.render.MessageUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class JumpCache extends PluginAddon<JumpsController> implements PluginController, ListenerAdapter {

    @NotNull private final String name;

    @NotNull private final ConfigSection configuration;

    @NotNull private final World world;
    private final float walkSpeed;

    @NotNull private final Location lobby;

    @NotNull private final JumpLocation start;
    @NotNull private final JumpLocation finish;
    @NotNull private final Map<Integer, JumpLocation> checkpoints;

    @NotNull private final JumpLeaderboard leaderboard;

    public JumpCache(@NotNull JumpsController module, @NotNull String name, @NotNull ConfigSection configuration) {
        super(module);

        this.name = name;

        this.configuration = configuration;

        this.world = ConfigUtils.retrieveWorld(module.getInstance(), configuration.of());

        this.walkSpeed = (float) ((double) configuration.require("walk-speed"));

        this.lobby = ConfigUtils.retrieveLocation(world, configuration.of("lobby"));

        this.start = JumpsController.retrieveLocation(world, configuration.of("start"));
        this.finish = JumpsController.retrieveLocation(world, configuration.of("finish"));
        this.checkpoints = configuration.of("checkpoints").keys((key, section) -> Integer.parseInt(key), (type, section) -> JumpsController.retrieveLocation(world, section), false);

        this.leaderboard = new JumpLeaderboard(module, this, configuration.of("leaderboard"));
    }

    @NotNull
    public LinkedHashMap<OfflinePlayer, Integer> retrieveRecords(int limit) {
        @NotNull final LinkedHashMap<OfflinePlayer, Integer> records = new LinkedHashMap<>();

        try {
            @NotNull final ResultSet results = module.getInstance().getDatabaseController().executeQuery("SELECT * FROM jumps_records WHERE jump_name = ? ORDER BY time ASC LIMIT ?", name, limit);

            while (results.next()) {
                try {
                    @NotNull final UUID uuid = UUID.fromString(results.getString("player_uuid"));
                    @Nullable final OfflinePlayer offlinePlayer = module.getServer().getOfflinePlayer(uuid);
                    if(offlinePlayer == null) continue;

                    final int time = results.getInt("time");

                    records.put(offlinePlayer, time);
                } catch (IllegalArgumentException ignored) {}
            }

            module.getInstance().getDatabaseController().closeResults(results);
        } catch (SQLException exception) {
            throw new IllegalStateException("Impossible de récupérer les records de temps.");
        }

        return records;
    }

    @Override
    public void load() {
        leaderboard.update();

        module.getInstance().getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        module.getInstance().getEventsController().unregisterEventAdapter(this);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;

        @Nullable final Block interactedBlock = event.getClickedBlock();
        if(interactedBlock == null) return;

        @NotNull final Location interactedLocation = interactedBlock.getLocation();
        @Nullable final InteractionType interactionType = InteractionType.retrieveInteractionType(this, interactedLocation);
        if(interactionType == null) return;

        @NotNull final HubPlayer hubPlayer = module.getInstance().getPlayersController().retrieveUserCache(event.getPlayer());
        @Nullable final JumpPlayer jumpPlayer = module.retrieveJumpPlayer(hubPlayer);

        try {
            switch (interactionType) {
                case START: {
                    if(jumpPlayer != null) return;

                    module.addPlayer(this, hubPlayer);

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
            module.removePlayer(hubPlayer);

            MessageUtils.sendMessage(module, hubPlayer.getAudience(), exception);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        @NotNull final HubPlayer hubPlayer = module.getInstance().getPlayersController().retrieveUserCache(event.getPlayer());
        @Nullable final JumpPlayer jumpPlayer = module.retrieveJumpPlayer(hubPlayer);
        if(jumpPlayer == null) return;

        if(hubPlayer.getBukkitPlayer().getLocation().getY() >= jumpPlayer.retrieveLatestLocation().getLowerY()) return;

        jumpPlayer.back();
    }

    public enum InteractionType {

        START,
        CHECKPOINT,
        FINISH;

        @Nullable
        public static InteractionType retrieveInteractionType(@NotNull JumpCache jumpCache, @NotNull Location interactedLocation) {
            if(interactedLocation.distance(jumpCache.getStart()) == 0) {
                return START;
            } else if(interactedLocation.distance(jumpCache.getFinish()) == 0) {
                return FINISH;
            } else if(jumpCache.getCheckpoints().values().stream().anyMatch(jumpLocation -> jumpLocation.distance(interactedLocation) == 0)) {
                return CHECKPOINT;
            }

            return null;
        }

    }

}
