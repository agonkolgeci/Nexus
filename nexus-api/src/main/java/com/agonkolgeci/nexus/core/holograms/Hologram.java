package com.agonkolgeci.nexus.core.holograms;

import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.utils.render.EntityUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Hologram extends AbstractAddon<HologramsManager> {

    public static final double COMPONENT_LINE_SPACING = 0.25;
    public static final double ITEM_STACK_LINE_SPACING = 0.6;

    @NotNull private final World world;

    @NotNull private final Location location;
    @NotNull private final Direction direction;

    @NotNull private final Component title;

    @NotNull private final UUID base;
    @NotNull private final LinkedList<UUID> lines;

    public Hologram(@NotNull HologramsManager instance, @NotNull Location location, @NotNull Component title, @NotNull Direction direction) {
        super(instance);

        this.world = location.getWorld();

        this.location = location;

        this.title = title;
        this.direction = direction;

        this.base = createLine(title, location);
        this.lines = new LinkedList<>();
    }

//    public Hologram(@NotNull EntitiesController module, @NotNull Location location, @NotNull ItemStack itemStack, @NotNull Direction direction) {
//        this(module, location, direction);
//
//        addLine(itemStack);
//    }

    @NotNull
    protected List<Entity> retrieveEntities() {
        return lines.stream().map(uuid -> (Entity) EntityUtils.retrieveEntity(world, uuid)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @NotNull
    public Location retrieveLastLocation() {
        if(!lines.isEmpty()) {
            @Nullable final Entity line = EntityUtils.retrieveEntity(world, lines.getLast());
            if(line != null) {
                return line.getLocation();
            }
        }

        return direction.transformLocation(location, COMPONENT_LINE_SPACING * 2);
    }

    public void remove() {
        EntityUtils.removeEntity(world, base);

        this.clearLines();
    }

    public void clearLines() {
        this.retrieveEntities().forEach(Entity::remove);
        this.lines.clear();
    }

    public void move(double x, double z) {
        for (@NotNull final Entity entity : retrieveEntities()) {
            @NotNull final Location newLocation = entity.getLocation().clone();

            newLocation.setX(x);
            newLocation.setZ(z);

            entity.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @NotNull
    public Hologram addLine(@NotNull Component text) {
        lines.add(createLine(text, retrieveLastLocation()));

        return this;
    }

    @NotNull
    public Hologram updateLines(@NotNull Component... lines) {
        this.clearLines();

        for(@NotNull final Component line : lines) {
            this.addLine(line);
        }

        return this;
    }

    @NotNull
    protected UUID createLine(@NotNull Component text, @NotNull Location parent) {
        @NotNull final ArmorStand armorStand = EntityUtils.createEntity(direction.transformLocation(parent.clone(), COMPONENT_LINE_SPACING), ArmorStand.class);

        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(LegacyComponentSerializer.legacySection().serialize(text));
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setSmall(true);

        return armorStand.getUniqueId();
    }

//    @NotNull
//    public Hologram addLine(@NotNull ItemStack itemStack) {
//        @NotNull final Item item = EntityUtils.createEntity(nextLocation.clone(), Item.class);
//
//        item.setGravity(false);
//        item.setVelocity(new Vector(0, 0, 0));
//        item.setCanMobPickup(false);
//        item.setCanPlayerPickup(false);
//        item.setUnlimitedLifetime(true);
//        item.setWillAge(false);
//
//        item.setItemStack(itemStack);
//
//        direction.translateLocation(nextLocation, ITEM_STACK_LINE_SPACING);
//
//        return registerLine(item);
//    }

    public enum Direction {

        UP {
            @Override
            public @NotNull Location transformLocation(@NotNull Location location, double value) {
                return location.clone().add(0, value, 0);
            }
        },

        DOWN {
            @Override
            public @NotNull Location transformLocation(@NotNull Location location, double value) {
                return location.clone().subtract(0, value, 0);
            }
        }

        ;

        @NotNull public abstract Location transformLocation(@NotNull Location location, double value);

    }
}
