package com.agonkolgeci.nexus_hub.core.jumps;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.core.binder.item.ItemInteractCallback;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.ui.TextMessaging;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLocation;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class JumpsManager extends PluginManager<NexusHub> implements PluginAdapter, ListenerAdapter {

    public static final TextMessaging MESSAGING = new TextMessaging("Parcours", NamedTextColor.YELLOW);

    @NotNull private final ConfigSection configuration;

    @NotNull private final List<JumpManager> jumps;

    @NotNull private final Map<Player, JumpPlayer> players;
    @NotNull private final Map<Integer, ItemStack> items;

    public JumpsManager(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.jumps = configuration.keys((key, config) -> new JumpManager(instance, this, key, config), false);

        this.players = new HashMap<>();
        this.items = new HashMap<>() {{
            put(3, InteractionsBinder.bind(new ItemBuilder(Material.LIGHT_WEIGHTED_PRESSURE_PLATE).displayName(Component.text("Revenir au checkpoint", NamedTextColor.GREEN, TextDecoration.BOLD)).build(), (ItemInteractCallback) (player, itemStack) -> {
                JumpsManager.this.computeIfPresent(player, JumpPlayer::back);
            }));

            put(5, InteractionsBinder.bind(new ItemBuilder(Material.BARRIER).displayName(Component.text("Quitter le jump", NamedTextColor.RED, TextDecoration.BOLD)).build(), (ItemInteractCallback) (player, itemStack) -> {
                JumpsManager.this.computeIfPresent(player, JumpPlayer::leave);
            }));
        }};
    }

    @Override
    public void load() {
        instance.getEventsManager().registerAdapter(this);

        jumps.forEach(JumpManager::load);
    }

    @Override
    public void unload() {
        instance.getEventsManager().unregisterAdapter(this);

        jumps.forEach(JumpManager::unload);
    }

    @NotNull
    public static JumpLocation retrieveLocation(@NotNull World world, @NotNull ConfigSection configuration) {
        final double x = configuration.require("x");
        final double y = configuration.require("y");
        final double z = configuration.require("z");

        final float yaw = (float) ((double) configuration.get("yaw", 0.0));
        final float pitch = (float) ((double) configuration.get("pitch", 0.0));

        final double lowerY = configuration.get("lowerY", y);

        return new JumpLocation(world, x, y, z, yaw, pitch, lowerY);
    }

    @NotNull
    public Component retrieveTimer(int time) {
        final int hours = (time / 20) / 3600;
        final int minutes = ((time / 20) % 3600) / 60;
        final int seconds = (time / 20) % 60;
        final int ticks = time % 20;

        return Component.text(String.format("%02d:%02d:%02d,%02d", hours, minutes, seconds, ticks), NamedTextColor.YELLOW, TextDecoration.BOLD);
    }

    public void computeIfPresent(@NotNull Player player, @NotNull Consumer<JumpPlayer> consumer) {
        @Nullable final JumpPlayer jumpPlayer = this.getJumpPlayer(player);
        if(jumpPlayer == null) return;

        consumer.accept(jumpPlayer);
    }

    public void addPlayer(@NotNull JumpManager jumpManager, @NotNull Player player) {
        if(isInJump(player)) throw new IllegalStateException("This player is in a jump !");

        @NotNull final JumpPlayer jumpPlayer = new JumpPlayer(this, jumpManager, player);

        players.put(player, jumpPlayer);
        jumpPlayer.start();
    }

    public void removePlayer(@NotNull Player player) {
        @NotNull final JumpPlayer jumpPlayer = Objects.requireNonNull(getJumpPlayer(player), "This player is not in a jump !");

        players.remove(player);
        jumpPlayer.stop();
    }

    @Nullable
    public JumpPlayer getJumpPlayer(@NotNull Player player) {
        return players.getOrDefault(player, null);
    }

    public boolean isInJump(@NotNull Player player) {
        return players.containsKey(player);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        @NotNull final Player player = event.getPlayer();

        if(isInJump(player)) {
            this.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlateInteract(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;

        @Nullable final Block interactedBlock = event.getClickedBlock();
        if(interactedBlock == null) return;

        switch (interactedBlock.getType()) {
            case LIGHT_WEIGHTED_PRESSURE_PLATE, HEAVY_WEIGHTED_PRESSURE_PLATE -> {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }

}
