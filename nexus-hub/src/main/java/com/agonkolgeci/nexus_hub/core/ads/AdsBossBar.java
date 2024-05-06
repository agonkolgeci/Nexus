package com.agonkolgeci.nexus_hub.core.ads;

import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginScheduler;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.objects.list.CircularQueue;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AdsBossBar extends AbstractAddon<AdsManager> implements PluginAdapter, PluginScheduler {

    @NotNull private final ConfigSection configuration;

    private final double delay;
    private final double period;

    @NotNull private final CircularQueue<String> messages;

    @NotNull private final BossBar bossBar;

    @Nullable private BukkitTask currentTask;

    @NotNull private final List<HubPlayer> targetAudiences;


    public AdsBossBar(@NotNull AdsManager module, @NotNull ConfigSection configuration) {
        super(module);

        this.configuration = configuration;

        this.delay = configuration.require("delay");
        this.period = configuration.require("period");

        this.messages = new CircularQueue<>(configuration.require("messages"));

        this.bossBar = BossBar.bossBar(Component.space(), 1F, ObjectUtils.fetchObject(BossBar.Color.class, configuration.require("color"), BossBar.Color.WHITE), ObjectUtils.fetchObject(BossBar.Overlay.class, configuration.require("overlay"), BossBar.Overlay.PROGRESS));

        this.targetAudiences = new ArrayList<>();
    }

    @Override
    public void load() throws Exception {
        if(messages.isEmpty()) {
            module.getLogger().warning("There are no messages configured for ads in the bossBar.");
        }

        if(!isRunning()) {
            this.start();
        }
    }

    @Override
    public void unload() {
        if(isRunning()) {
            this.stop();
        }
    }

    @Override
    public @NotNull BukkitTask start() {
        if(isRunning()) throw new TaskRunningException();

        return this.currentTask = new BukkitRunnable() {
            private final long interval = ObjectUtils.retrieveTicks(period);
            private long ticks = 0;

            @Nullable Component currentMessage;

            @Override
            public void run() {
                if(ticks == 0) {
                    currentMessage = MessageUtils.MM_SERIALIZER.deserializeOrNull(messages.next());
                    if(currentMessage == null) {
                        stop();

                        return;
                    }

                    bossBar.name(currentMessage);

                    ticks = interval;
                }

                bossBar.progress((float) ticks / interval);

                ticks--;
            }
        }.runTaskTimer(module.getPlugin(), ObjectUtils.retrieveTicks(delay), 1);
    }

    @Override
    public void stop() {
        if(!isRunning()) throw new TaskNotRunningException();

        this.currentTask.cancel();
        this.currentTask = null;
    }

    public void loadPlayer(@NotNull HubPlayer hubPlayer) {
        hubPlayer.getAudience().showBossBar(bossBar);
        targetAudiences.add(hubPlayer);
    }

    public void unloadPlayer(@NotNull HubPlayer hubPlayer) {
        hubPlayer.getAudience().hideBossBar(bossBar);
        targetAudiences.remove(hubPlayer);
    }

}


