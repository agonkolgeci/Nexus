package com.agonkolgeci.nexus_hub.core.jumps.components;

import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.plugin.PluginScheduler;
import com.agonkolgeci.nexus.utils.world.EffectsUtils;
import com.agonkolgeci.nexus_hub.core.jumps.JumpManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
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
public class JumpPlayer extends AbstractAddon<JumpsManager> implements PluginScheduler {

    @NotNull private final JumpManager jumpManager;
    @NotNull private final HubPlayer hubPlayer;

    @Nullable private Map.Entry<Integer, JumpLocation> checkpoint;

    private int record;
    private int elapsed;

    @Nullable private BukkitTask currentTask;

    public JumpPlayer(@NotNull JumpsManager module, @NotNull JumpManager jumpManager, @NotNull HubPlayer hubPlayer) {
        super(module);

        this.jumpManager = jumpManager;
        this.hubPlayer = hubPlayer;

        this.checkpoint = null;

        this.record = retrieveRecord();
        this.elapsed = 0;

        this.currentTask = null;
    }

    public int retrieveRecord() {
        int record = Integer.MAX_VALUE;

        try {
            @NotNull final ResultSet results = module.getInstance().getDatabaseManager().executeQuery("SELECT * FROM jumps_records WHERE jump_name = ? AND player_uuid = ?", jumpManager.getName(), hubPlayer.getUniqueId().toString());

            if(results.next()) {
                record = results.getInt("time");
            }

            module.getInstance().getDatabaseManager().closeResults(results);
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

        module.getInstance().getDatabaseManager().executeUpdate("INSERT INTO jumps_records(jump_name, player_uuid, time) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE time = VALUES(time)", jumpManager.getName(), hubPlayer.getUniqueId().toString(), record);
    }

    @NotNull
    public BukkitTask start() {
        if(currentTask != null) throw new TaskRunningException();

        module.getInstance().getSpawnManager().unloadPlayer(hubPlayer);
        module.getInstance().getAdsManager().getAdsActionBar().unloadPlayer(hubPlayer);

        hubPlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
        hubPlayer.getPlayer().setWalkSpeed(jumpManager.getWalkSpeed());

        module.getItems().forEach((i, itemStack) -> hubPlayer.getPlayer().getInventory().setItem(i, itemStack));

        hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous venez de commencez le").appendSpace().append(Component.text(jumpManager.getName(), NamedTextColor.YELLOW)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD)));

        if(hasRecord()) {
            hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text("Votre record personnel à battre est de").appendSpace().append(module.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN)));
        } else {
            hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous n'avez aucun record personnel.").colorIfAbsent(NamedTextColor.GRAY)));
        }

        return this.currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                hubPlayer.getPlayer().sendActionBar(module.retrieveTimer(elapsed));

                elapsed++;
            }
        }.runTaskTimer(module.getPlugin(), 0, 1);
    }

    public void stop() {
        module.getInstance().getSpawnManager().loadPlayer(hubPlayer);
        module.getInstance().getAdsManager().getAdsActionBar().loadPlayer(hubPlayer);

        if(currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
    }

    public void leave() {
        module.removePlayer(hubPlayer);

        hubPlayer.getPlayer().teleport(jumpManager.getLobby());

        hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous venez de quitter le").appendSpace().append(Component.text(jumpManager.getName(), NamedTextColor.YELLOW)).append(Component.text("."))));
    }

    public void finish() {
        if(retrieveNextCheckpoint() != null) {
            throw new IllegalStateException("Vous n'avez pas pris tous les checkpoints !");
        }

        module.removePlayer(hubPlayer);

        hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text("Vous avez terminé le").appendSpace().append(Component.text(jumpManager.getName(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GOLD)));

        final boolean newRecord = !hasRecord();
        final boolean beatRecord = elapsed < record && !newRecord;

        if(newRecord || beatRecord) {
            this.setRecord(elapsed);

            if(beatRecord) {
                hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.info(Component.text("Félicitations, vous avez battu votre record personnel !").colorIfAbsent(NamedTextColor.GREEN)));
            }

            hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.info(Component.text("Votre nouveau record personnel est de").appendSpace().append(module.retrieveTimer(record)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GREEN)));
        }

        hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.success(Component.text().append(hubPlayer.getDisplayName()).appendSpace().append(Component.text("vient de terminer le")).appendSpace().append(Component.text(jumpManager.getName(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).appendSpace().append(Component.text("!")).colorIfAbsent(NamedTextColor.GREEN).build()));
        EffectsUtils.spawnFireworks(hubPlayer.getPlayer().getLocation(), 5, 5);

        jumpManager.getLeaderboard().update();
    }

    public void back() {
        hubPlayer.getPlayer().teleport(retrieveLatestLocation().toCenter());

        if(checkpoint == null) {
            elapsed = 0;
        }
    }

    public void setCheckpoint(@NotNull Map.Entry<Integer, JumpLocation> checkpoint) {
        this.checkpoint = checkpoint;

        hubPlayer.getPlayer().sendMessage(JumpsManager.MESSAGING.info(Component.text("Vous avez atteint le checkpoint").appendSpace().append(Component.text("#" + checkpoint.getKey(), NamedTextColor.YELLOW)).appendSpace().append(Component.text("en")).appendSpace().append(module.retrieveTimer(elapsed)).append(Component.text(".")).colorIfAbsent(NamedTextColor.GOLD)));
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
