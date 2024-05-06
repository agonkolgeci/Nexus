package com.agonkolgeci.nexus_hub.core.jumps;

import com.agonkolgeci.nexus.api.interactions.PlayerItemInteractCallback;
import com.agonkolgeci.nexus.api.players.events.PlayerLogoutEvent;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.render.MessageUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpLocation;
import com.agonkolgeci.nexus_hub.core.jumps.components.JumpPlayer;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class JumpsManager extends PluginManager<NexusHub> implements PluginAdapter, MessageUtils.Stylizable {

    @NotNull private final ConfigSection configuration;

    @NotNull private final List<JumpManager> jumps;

    @NotNull private final Map<HubPlayer, JumpPlayer> players;
    @NotNull private final Map<Integer, ItemStack> items;

    public JumpsManager(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.jumps = configuration.keys((key, config) -> new JumpManager(instance, this, key, config), false);

        this.players = new HashMap<>();
        this.items = new HashMap<Integer, ItemStack>() {{
            put(3, handleInteraction(new ItemBuilder<>(Material.IRON_PLATE).displayName(Component.text("Revenir au checkpoint", NamedTextColor.GREEN, TextDecoration.BOLD)).toItemStack(), JumpPlayer::back));
            put(5, handleInteraction(new ItemBuilder<>(Material.BARRIER).displayName(Component.text("Quitter le jump", NamedTextColor.RED, TextDecoration.BOLD)).toItemStack(), JumpPlayer::leave));
        }};
    }

    @Override
    public @NotNull Component getPrefix() {
        return Component.text("Parcours", NamedTextColor.YELLOW);
    }

    @Override
    public void load() {
        jumps.forEach(JumpManager::load);
    }

    @Override
    public void unload() {
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

    @NotNull
    public ItemStack handleInteraction(@NotNull ItemStack itemStack, @NotNull Consumer<JumpPlayer> consumer) {
        instance.getInteractionsManager().handle(itemStack, (PlayerItemInteractCallback) (player, i) -> {
            @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrievePlayerCache(player);

            @Nullable final JumpPlayer jumpPlayer = retrieveJumpPlayer(hubPlayer);
            if(jumpPlayer == null) return;

            consumer.accept(jumpPlayer);
        });

        return itemStack;
    }

    public void addPlayer(@NotNull JumpManager jumpManager, @NotNull HubPlayer hubPlayer) {
        if(isInJump(hubPlayer)) throw new IllegalStateException("This player is in a jump !");

        @NotNull final JumpPlayer jumpPlayer = new JumpPlayer(this, jumpManager, hubPlayer);

        players.put(hubPlayer, jumpPlayer);
        jumpPlayer.start();
    }

    public void removePlayer(@NotNull HubPlayer hubPlayer) {
        @NotNull final JumpPlayer jumpPlayer = Objects.requireNonNull(retrieveJumpPlayer(hubPlayer), "This player is not in a jump !");

        players.remove(hubPlayer);
        jumpPlayer.stop();
    }

    @Nullable
    public JumpPlayer retrieveJumpPlayer(@NotNull HubPlayer hubPlayer) {
        return players.getOrDefault(hubPlayer, null);
    }

    public boolean isInJump(@NotNull HubPlayer hubPlayer) {
        return players.containsKey(hubPlayer);
    }

    @EventHandler
    public void onPlayerLogout(@NotNull PlayerLogoutEvent event) {
        @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrievePlayerCache(event.getPlayer());

        if(isInJump(hubPlayer)) {
            this.removePlayer(hubPlayer);
        }
    }

    @EventHandler
    public void onPlateInteract(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.PHYSICAL) return;

        @Nullable final Block interactedBlock = event.getClickedBlock();
        if(interactedBlock == null) return;

        switch (interactedBlock.getType()) {
            case IRON_PLATE:
            case GOLD_PLATE: {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);

                break;
            }
        }
    }

}
