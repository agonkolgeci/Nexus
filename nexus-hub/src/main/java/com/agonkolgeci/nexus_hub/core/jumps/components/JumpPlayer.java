package com.agonkolgeci.nexus_hub.core.jumps.components;

import com.agonkolgeci.nexus.utils.world.EffectsUtils;
import com.agonkolgeci.nexus_hub.core.jumps.JumpManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Getter
public class JumpPlayer {

    @NotNull private final JumpsManager jumpsManager;

    @NotNull private final JumpManager jumpManager;
    @NotNull private final Player player;

    @Nullable private Map.Entry<Integer, JumpLocation> checkpoint;

    private Duration record;
    private Instant start;

    @Nullable private BukkitTask currentTask;

    public JumpPlayer(@NotNull JumpsManager jumpsManager, @NotNull JumpManager jumpManager, @NotNull Player player) {
        this.jumpsManager = jumpsManager;
        this.jumpManager = jumpManager;
        this.player = player;

        this.checkpoint = null;

        this.record = retrieveRecord();

        this.currentTask = null;
    }

    public Duration retrieveRecord() {
        Duration record = null;

        try {
            @NotNull final ResultSet results = jumpsManager.getInstance().getDatabaseManager().executeQuery("SELECT * FROM jumps_records WHERE jump_name = ? AND player_uuid = ?", jumpManager.getName(), player.getUniqueId().toString());

            if(results.next()) {
                record = Duration.ofMillis(results.getLong("time"));
            }

            jumpsManager.getInstance().getDatabaseManager().closeResults(results);
        } catch (SQLException exception) {
            throw new IllegalStateException("Impossible de récupérer votre record de temps.");
        }

        return record;
    }

    public boolean hasRecord() {
        return record != null;
    }

    public void setRecord(Duration record) {
        this.record = record;

        jumpsManager.getInstance().getDatabaseManager().executeUpdate("INSERT INTO jumps_records(jump_name, player_uuid, time) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE time = VALUES(time)", jumpManager.getName(), player.getUniqueId().toString(), record.toMillis());
    }

    public void start() {
        jumpsManager.getInstance().getSpawnManager().removePlayer(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setWalkSpeed(jumpManager.getWalkSpeed());

        jumpsManager.getItems().forEach((i, itemStack) -> player.getInventory().setItem(i, itemStack));

        player.sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous venez de commencez le").appendSpace().append(jumpManager.getDisplayName().color(NamedTextColor.YELLOW)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD)));

        if(hasRecord()) {
            player.sendMessage(JumpsManager.MESSAGING.success(Component.text("Votre record personnel à battre est de").appendSpace().append(jumpsManager.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN)));
        } else {
            player.sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous n'avez aucun record personnel.").colorIfAbsent(NamedTextColor.GRAY)));
        }

        this.start = Instant.now();
        this.currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.sendActionBar(jumpsManager.retrieveTimer(Duration.between(start, Instant.now())));
            }
        }.runTaskTimer(jumpsManager.getPlugin(), 0, 1);
    }

    public void stop() {
        jumpsManager.getInstance().getSpawnManager().addPlayer(player);

        if(currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public void leave() {
        jumpsManager.removePlayer(player);

        player.teleport(jumpManager.getLobby());

        player.sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous venez de quitter le").appendSpace().append(jumpManager.getDisplayName().color(NamedTextColor.YELLOW)).append(Component.text("."))));
    }

    public void finish() {
        if(retrieveNextCheckpoint() != null) {
            throw new IllegalStateException("Vous n'avez pas pris tous les checkpoints !");
        }

        jumpsManager.removePlayer(player);

        final Duration finalTime = Duration.between(start, Instant.now());

        player.sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous avez terminé le").appendSpace().append(jumpManager.getDisplayName().color(NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(jumpsManager.retrieveTimer(finalTime)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GOLD)));

        final boolean newRecord = !hasRecord();
        final boolean beatRecord = finalTime.compareTo(record) < 0 && !newRecord;

        if(newRecord || beatRecord) {
            this.setRecord(finalTime);

            if(beatRecord) {
                player.sendMessage(JumpsManager.MESSAGING.info(Component.text("Félicitations, vous avez battu votre record personnel !").colorIfAbsent(NamedTextColor.GREEN)));
            }

            player.sendMessage(JumpsManager.MESSAGING.info(Component.text("Votre nouveau record personnel est de").appendSpace().append(jumpsManager.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN)));
        }

        jumpsManager.getInstance().getServer().sendMessage(JumpsManager.MESSAGING.success(Component.text().append(player.displayName()).appendSpace().append(Component.text("vient de terminer le")).appendSpace().append(jumpManager.getDisplayName().color(NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(jumpsManager.retrieveTimer(finalTime)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GREEN).build()));
        EffectsUtils.spawnFireworks(player.getLocation(), 5, 5);

        jumpManager.getLeaderboard().update();
    }

    public void back() {
        player.teleport(retrieveLatestLocation().toCenter());

        if(checkpoint == null) {
            start = Instant.now();
        }
    }

    public void setCheckpoint(@NotNull Map.Entry<Integer, JumpLocation> checkpoint) {
        this.checkpoint = checkpoint;

        final Duration currentTime = Duration.between(start, Instant.now());

        player.sendMessage(JumpsManager.MESSAGING.info(Component.text("Vous avez atteint le checkpoint").appendSpace().append(Component.text("#" + checkpoint.getKey(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(jumpsManager.retrieveTimer(currentTime)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD)));
    }

    @Nullable
    public Map.Entry<Integer, JumpLocation> retrieveNextCheckpoint() {
        if(jumpManager.getCheckpoints().isEmpty()) return null;
        if(checkpoint == null) return jumpManager.getCheckpoints().entrySet().stream().findFirst().orElse(null);

        return jumpManager.getCheckpoints().entrySet().stream().filter(entry -> entry.getKey() == (checkpoint.getKey()+1)).filter(entry -> entry.getValue() != checkpoint.getValue()).findFirst().orElse(null);
    }

    @NotNull
    public JumpLocation retrieveLatestLocation() {
        return checkpoint != null ? checkpoint.getValue() : jumpManager.getStart();
    }

}
