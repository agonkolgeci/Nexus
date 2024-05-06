package com.agonkolgeci.nexus_hub.core.spawn;

import com.agonkolgeci.nexus.api.interactions.PlayerItemInteractCallback;
import com.agonkolgeci.nexus.api.players.events.PlayerLogoutEvent;
import com.agonkolgeci.nexus.api.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.common.config.ConfigUtils;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.render.PlayerUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.gui.main.GuiMain;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SpawnManager extends PluginManager<NexusHub> implements PluginAdapter {

    public static final float SPAWN_WALK_SPEED = PlayerUtils.DEFAULT_WALK_SPEED * 1.5F;
    public static final float SPAWN_FLY_SPEED = PlayerUtils.DEFAULT_FLY_SPEED * 2F;

    @NotNull private final ConfigSection configuration;

    @NotNull private final World world;
    @NotNull private final Location center;

    @NotNull private final Map<Integer, ItemStack> items;

    public SpawnManager(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.world = ConfigUtils.retrieveWorld(instance, configuration.of());
        this.center = ConfigUtils.retrieveLocation(world, configuration.of("center"));

        this.items = new HashMap<Integer, ItemStack>() {{
            put(4, instance.getInteractionsManager().handle(new ItemBuilder<>(Material.COMPASS).displayName(Component.text("Menu Principal", NamedTextColor.GREEN, TextDecoration.BOLD)).toItemStack(), (PlayerItemInteractCallback) (player, itemStack) -> instance.getGuiManager().openGui(player, new GuiMain(instance))));
        }};
    }

    @Override
    public void load() throws Exception {
        this.setupWorld();

        instance.getCommandsManager().registerCommandAdapter("spawn", new SpawnCommand(this));
    }

    @Override
    public void unload() {
    }

    public void setupWorld() {
        world.setStorm(false);
        world.setThundering(false);
        world.setTime(6000L);
        world.setGameRuleValue("doDaylightCycle", "false");
    }

    public void loadPlayer(@NotNull HubPlayer hubPlayer) {
        PlayerUtils.clearPlayer(hubPlayer.getPlayer());
        PlayerUtils.giveItems(hubPlayer.getPlayer(), items);

        hubPlayer.getPlayer().setGameMode(hubPlayer.getPlayer().isOp() ? GameMode.CREATIVE : GameMode.ADVENTURE);
        hubPlayer.getPlayer().setWalkSpeed(SPAWN_WALK_SPEED);
        hubPlayer.getPlayer().setFlySpeed(SPAWN_FLY_SPEED);
    }

    public void unloadPlayer(@NotNull HubPlayer hubPlayer) {
        PlayerUtils.clearPlayer(hubPlayer.getPlayer());

        hubPlayer.getPlayer().setWalkSpeed(PlayerUtils.DEFAULT_WALK_SPEED);
        hubPlayer.getPlayer().setFlySpeed(PlayerUtils.DEFAULT_FLY_SPEED);
    }

    public void teleportPlayer(@NotNull Player player) {
        player.teleport(center);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.teleportPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerReady(@NotNull PlayerReadyEvent event) {
        this.loadPlayer(instance.getPlayersController().retrievePlayerCache(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLogout(@NotNull PlayerLogoutEvent event) {
        this.unloadPlayer(instance.getPlayersController().retrievePlayerCache(event.getPlayer()));
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageEvent event) {
        @NotNull final Entity entity = event.getEntity();
        if(entity.getWorld() != world) return;

        switch (event.getCause()) {
            case VOID: {
                entity.teleport(center);
            }

            default: {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(@NotNull FoodLevelChangeEvent event) {
        @NotNull final HumanEntity humanEntity = event.getEntity();
        if(humanEntity.getWorld() != world) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        @NotNull final Block block = event.getBlock();
        if(block.getWorld() != world) return;

        @NotNull final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        @NotNull final Block block = event.getBlock();
        if(block.getWorld() != world) return;

        @NotNull final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(@NotNull BlockExplodeEvent event) {
        event.blockList().removeIf(block -> block.getWorld() == world);
    }

    @EventHandler
    public void onBlockBurn(@NotNull BlockBurnEvent event) {
        @NotNull final Block block = event.getBlock();
        if(block.getWorld() != world) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(@NotNull WeatherChangeEvent event) {
        @NotNull final World world = event.getWorld();
        if(world != this.world) return;

        event.setCancelled(true);
    }
}
