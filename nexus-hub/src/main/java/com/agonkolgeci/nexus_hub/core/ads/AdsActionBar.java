package com.agonkolgeci.nexus_hub.core.ads;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus.utils.objects.list.CircularQueue;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AdsActionBar implements PluginAdapter {

    @NotNull private final AdsManager adsManager;
    @NotNull private final ConfigSection configuration;

    private final int delay;
    private final int period;

    @NotNull private final CircularQueue<String> messages;
    @NotNull private final List<Audience> audiences;

    @NotNull private final BukkitTask currentTask;

    public AdsActionBar(@NotNull AdsManager adsManager, @NotNull ConfigSection configuration) {
        this.adsManager = adsManager;
        this.configuration = configuration;

        this.delay = configuration.require("delay");
        this.period = configuration.require("period");

        this.messages = new CircularQueue<>(configuration.require("messages"));

        this.audiences = new ArrayList<>();

        this.currentTask = new BukkitRunnable() {
            @Nullable private Component message;

            private final int interval = period;
            private int seconds = 0;

            @Override
            public void run() {
                if(seconds == 0) {
                    message = MessageUtils.MM_SERIALIZER.deserializeOrNull(messages.next());
                    seconds = interval;
                }

                if(message == null) {
                    this.cancel();

                    return;
                }

                audiences.forEach(audience -> audience.sendActionBar(message));

                seconds--;
            }
        }.runTaskTimer(adsManager.getPlugin(), ObjectUtils.toTicks(delay), ObjectUtils.toTicks(1));
    }

    @Override
    public void load() throws Exception {
        if(messages.isEmpty()) {
            adsManager.getLogger().warning("There are no messages configured for ads in the actionBar.");
        }
    }

    @Override
    public void unload() {

    }

    public void addAudience(@NotNull Audience audience) {
        audiences.add(audience);
    }

    public void removeAudience(@NotNull Audience audience) {
        audiences.remove(audience);
    }

}


