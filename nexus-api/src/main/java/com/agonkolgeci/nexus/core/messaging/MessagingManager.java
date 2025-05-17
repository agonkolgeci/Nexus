package com.agonkolgeci.nexus.core.messaging;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class MessagingManager extends PluginManager<NexusAPI> implements PluginAdapter {

    @NotNull private final Messenger messenger;

    @NotNull private final Map<PluginChannelType, Map<UUID, List<Consumer<ByteArrayDataInput>>>> listeners;

    public MessagingManager(@NotNull NexusAPI instance) {
        super(instance);

        this.messenger = instance.getServer().getMessenger();

        this.listeners = new HashMap<>();
    }

    @Override
    public void load() throws Exception {
        for(PluginChannelType channelType : PluginChannelType.values()) {
            messenger.registerOutgoingPluginChannel(instance.getPlugin(), channelType.getName());
            messenger.registerIncomingPluginChannel(instance.getPlugin(), channelType.getName(), (channelName, player, bytes) -> {
                @NotNull final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
                @Nullable final PluginChannelType type = ObjectUtils.fetchObject(PluginChannelType.class, channelName);
                if(type == null) return;

                if(!listeners.containsKey(type)) return;

                @NotNull final Map<UUID, List<Consumer<ByteArrayDataInput>>> targetListeners = listeners.get(type);
                if(!targetListeners.containsKey(player.getUniqueId())) return;

                @NotNull final List<Consumer<ByteArrayDataInput>> listeners = targetListeners.get(player.getUniqueId());
                listeners.forEach(listener -> listener.accept(input));
            });
        }
    }

    @Override
    public void unload() {

    }

    public void send(Player player, PluginChannelType channelType, Consumer<ByteArrayDataOutput> output) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();

        output.accept(out);

        player.sendPluginMessage(plugin, channelType.getName(), out.toByteArray());
    }

    public void wait(Player player, PluginChannelType channelType, Consumer<ByteArrayDataInput> input) {
        this.listeners.computeIfAbsent(channelType, k -> new HashMap<>()).computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(input);
    }
}
