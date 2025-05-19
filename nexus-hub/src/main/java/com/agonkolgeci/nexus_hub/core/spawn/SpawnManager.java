package com.agonkolgeci.nexus_hub.core.spawn;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.api.config.ConfigUtils;
import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.core.binder.item.ItemInteractCallback;
import com.agonkolgeci.nexus.core.gui.GuiManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.entity.PlayerUtils;
import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.gui.main.MainGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SpawnManager extends PluginManager<NexusHub> implements PluginAdapter, ListenerAdapter {

    public static final float SPAWN_WALK_SPEED = PlayerUtils.DEFAULT_WALK_SPEED * 1.5F;
    public static final float SPAWN_FLY_SPEED = PlayerUtils.DEFAULT_FLY_SPEED * 2F;

    @NotNull private final ConfigSection configuration;

    @NotNull private final World world;
    @NotNull private final Location center;

    @NotNull private final Map<Integer, ItemStack> hotbarItems;

    public SpawnManager(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.world = ConfigUtils.retrieveWorld(instance, configuration.of());
        this.center = ConfigUtils.retrieveLocation(world, configuration.of("center"));

        this.hotbarItems = new HashMap<>() {{
            put(4, InteractionsBinder.bind(new ItemBuilder(Material.COMPASS).displayName(Component.text("Menu Principal", NamedTextColor.GREEN, TextDecoration.BOLD)).build(), (ItemInteractCallback) (player, itemStack) -> GuiManager.openGui(player, new MainGui(instance))));
        }};;
    }

    @Override
    public void load() throws Exception {
        this.setupWorld();

        instance.getEventsManager().registerAdapter(this);

        instance.getCommandsManager().registerAdapter("spawn", new SpawnCommand(this));

        instance.getServer().getOnlinePlayers().forEach(this::addPlayer);
    }

    @Override
    public void unload() {
        instance.getEventsManager().unregisterAdapter(this);

        instance.getServer().getOnlinePlayers().forEach(this::removePlayer);
    }

    public void setupWorld() {
        world.setStorm(false);
        world.setThundering(false);
        world.setTime(6000L);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

    public void teleport(@NotNull Entity entity) {
        entity.teleport(center);
    }

    public void addPlayer(@NotNull Player player) {
        PlayerUtils.clearPlayer(player);
        PlayerUtils.giveItems(player, hotbarItems);

        player.setGameMode(player.isOp() ? GameMode.CREATIVE : GameMode.ADVENTURE);
        player.setWalkSpeed(SPAWN_WALK_SPEED);
        player.setFlySpeed(SPAWN_FLY_SPEED);

        instance.getAdsManager().getAdsActionBar().addAudience(player);
        instance.getAdsManager().getAdsBossBar().addAudience(player);
    }

    public void removePlayer(@NotNull Player player) {
        PlayerUtils.clearPlayer(player);

        player.setWalkSpeed(PlayerUtils.DEFAULT_WALK_SPEED);
        player.setFlySpeed(PlayerUtils.DEFAULT_FLY_SPEED);

        instance.getAdsManager().getAdsActionBar().removeAudience(player);
        instance.getAdsManager().getAdsBossBar().removeAudience(player);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        @NotNull final Player player = event.getPlayer();

        this.addPlayer(player);
        this.teleport(player);

        event.joinMessage(Component.empty().append(player.displayName()).appendSpace().append(Component.text("vient de rejoindre le Hub !", NamedTextColor.GREEN)));
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        @NotNull final Player player = event.getPlayer();

        this.removePlayer(player);

        event.quitMessage(Component.empty().append(player.displayName()).appendSpace().append(Component.text("vient de quitter le Hub !", NamedTextColor.RED)));
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageEvent event) {
        @NotNull final Entity entity = event.getEntity();
        if(entity.getWorld() != world) return;

        switch (event.getCause()) {
            case VOID: {
                this.teleport(entity);
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
