package com.agonkolgeci.nexus_hub.core.jumps.addons;

import com.agonkolgeci.nexus_hub.core.jumps.JumpCache;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsController;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLocation;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import com.agonkolgeci.nexus_api.plugin.PluginAddon;
import com.agonkolgeci.nexus_api.plugin.PluginScheduler;
import com.agonkolgeci.nexus_api.utils.render.EffectsUtils;
import com.agonkolgeci.nexus_api.utils.render.MessageUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Getter
public class JumpPlayer extends PluginAddon<JumpsController> implements PluginScheduler {

    @NotNull private final JumpCache jumpCache;
    @NotNull private final HubPlayer hubPlayer;

    @Nullable private Map.Entry<Integer, JumpLocation> checkpoint;

    private int record;
    private int elapsed;

    @Nullable private BukkitTask currentTask;

    public JumpPlayer(@NotNull JumpsController module, @NotNull JumpCache jumpCache, @NotNull HubPlayer hubPlayer) {
        super(module);

        this.jumpCache = jumpCache;
        this.hubPlayer = hubPlayer;

        this.checkpoint = null;

        this.record = retrieveRecord();
        this.elapsed = 0;

        this.currentTask = null;
    }

    public int retrieveRecord() {
        int record = Integer.MAX_VALUE;

        try {
            @NotNull final ResultSet results = module.getInstance().getDatabaseController().executeQuery("SELECT * FROM jumps_records WHERE jump_name = ? AND player_uuid = ?", jumpCache.getName(), hubPlayer.getUniqueId().toString());

            if(results.next()) {
                record = results.getInt("time");
            }

            module.getInstance().getDatabaseController().closeResults(results);
        } catch (SQLException exception) {
            throw new IllegalStateException("Impossible de récupérer votre record de temps.");
        }

        return record;
    }

    public boolean hasRecord() {
        return record != Integer.MAX_VALUE;
    }

    public void setRecord(int record) {
        this.record = record;

        module.getInstance().getDatabaseController().executeUpdate("INSERT INTO jumps_records(jump_name, player_uuid, time) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE time = VALUES(time)", jumpCache.getName(), hubPlayer.getUniqueId().toString(), record);
    }

    @NotNull
    public BukkitTask start() {
        if(currentTask != null) throw new TaskRunningException();

        module.getInstance().getSpawnController().unloadPlayer(hubPlayer);
        module.getInstance().getAdsController().getAdsActionBar().unloadPlayer(hubPlayer);

        hubPlayer.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
        hubPlayer.getBukkitPlayer().setWalkSpeed(jumpCache.getWalkSpeed());

        module.getItems().forEach((i, itemStack) -> hubPlayer.getBukkitPlayer().getInventory().setItem(i, itemStack));

        MessageUtils.sendMessage(MessageUtils.Type.SUCCESS, module, hubPlayer.getAudience(), Component.text("Vous venez de commencez le").appendSpace().append(Component.text(jumpCache.getName(), NamedTextColor.YELLOW)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD));

        if(hasRecord()) {
            MessageUtils.sendMessage(MessageUtils.Type.INFO, module, hubPlayer.getAudience(), Component.text("Votre record personnel à battre est de").appendSpace().append(module.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN));
        } else {
            MessageUtils.sendMessage(MessageUtils.Type.INFO, module, hubPlayer.getAudience(), Component.text("Vous n'avez aucun record personnel.").colorIfAbsent(NamedTextColor.GRAY));
        }

        return this.currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                hubPlayer.getAudience().sendActionBar(module.retrieveTimer(elapsed));

                elapsed++;
            }
        }.runTaskTimer(module.getPlugin(), 0, 1);
    }

    public void stop() {
        module.getInstance().getSpawnController().loadPlayer(hubPlayer);
        module.getInstance().getAdsController().getAdsActionBar().loadPlayer(hubPlayer);

        if(currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public void leave() {
        module.removePlayer(hubPlayer);

        hubPlayer.getBukkitPlayer().teleport(jumpCache.getLobby());

        MessageUtils.sendMessage(MessageUtils.Type.SUCCESS, module, hubPlayer.getAudience(), Component.text("Vous venez de quitter le").appendSpace().append(Component.text(jumpCache.getName(), NamedTextColor.YELLOW)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GRAY));
    }

    public void finish() {
        if(retrieveNextCheckpoint() != null) {
            throw new IllegalStateException("Vous n'avez pas pris tous les checkpoints !");
        }

        module.removePlayer(hubPlayer);

        MessageUtils.sendMessage(MessageUtils.Type.SUCCESS, module, hubPlayer.getAudience(), Component.text("Vous avez terminé le").appendSpace().append(Component.text(jumpCache.getName(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GOLD));

        final boolean newRecord = !hasRecord();
        final boolean beatRecord = elapsed < record && !newRecord;

        if(newRecord || beatRecord) {
            this.setRecord(elapsed);

            if(beatRecord) {
                MessageUtils.sendMessage(MessageUtils.Type.INFO, module, hubPlayer.getAudience(), Component.text("Félicitations, vous avez battu votre record personnel !").colorIfAbsent(NamedTextColor.GREEN));
            }

            MessageUtils.sendMessage(MessageUtils.Type.INFO, module, hubPlayer.getAudience(), Component.text("Votre nouveau record personnel est de").appendSpace().append(module.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN));
        }

        MessageUtils.sendMessage(MessageUtils.Type.SUCCESS, module.getAll(), Component.text().append(hubPlayer.getDisplayName()).appendSpace().append(Component.text("vient de terminer le")).appendSpace().append(Component.text(jumpCache.getName(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GREEN).build());
        EffectsUtils.spawnFireworks(hubPlayer.getBukkitPlayer().getLocation(), 5, 5);

        jumpCache.getLeaderboard().update();
    }

    public void back() {
        hubPlayer.getBukkitPlayer().teleport(retrieveLatestLocation().toCenter());
    }

    public void setCheckpoint(@NotNull Map.Entry<Integer, JumpLocation> checkpoint) {
        this.checkpoint = checkpoint;

        MessageUtils.sendMessage(MessageUtils.Type.SUCCESS, module, hubPlayer.getAudience(), Component.text("Vous avez atteint le checkpoint").appendSpace().append(Component.text("#" + checkpoint.getKey(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD));
    }

    @Nullable
    public Map.Entry<Integer, JumpLocation> retrieveNextCheckpoint() {
        if(jumpCache.getCheckpoints().isEmpty()) return null;
        if(checkpoint == null) return jumpCache.getCheckpoints().entrySet().stream().findFirst().orElse(null);

        return jumpCache.getCheckpoints().entrySet().stream().filter(entry -> entry.getKey() == (checkpoint.getKey()+1)).filter(entry -> entry.getValue() != checkpoint.getValue()).findFirst().orElse(null);
    }

    @NotNull
    public JumpLocation retrieveLatestLocation() {
        return checkpoint != null ? checkpoint.getValue() : jumpCache.getStart();
    }

}
