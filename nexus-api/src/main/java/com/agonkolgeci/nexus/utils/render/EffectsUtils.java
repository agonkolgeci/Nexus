package com.agonkolgeci.nexus.utils.render;

import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffectsUtils {

    public static void spawnFireworks(@NotNull Location location, int amount, int radius) {
        for(int i = 0; i < amount; i++) {
            @NotNull final Vector vector = new Vector(ObjectUtils.SPLITTABLE_RANDOM.nextInt(-radius, radius), ObjectUtils.SPLITTABLE_RANDOM.nextInt(radius), ObjectUtils.SPLITTABLE_RANDOM.nextInt(-radius, radius));
            @NotNull final Location randomLocation = location.clone().add(vector);

            @Nullable final World world = location.getWorld();
            if(world == null) return;

            @NotNull final Firework firework = (Firework) world.spawnEntity(randomLocation, EntityType.FIREWORK);
            @NotNull final FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(ObjectUtils.SPLITTABLE_RANDOM.nextInt(1, 2));

            @NotNull final FireworkEffect.Builder fireworkEffectBuilder = FireworkEffect.builder();
            fireworkEffectBuilder.withColor(ObjectUtils.retrieveRandomColor());
            fireworkEffectBuilder.withFade(ObjectUtils.retrieveRandomColor());
            fireworkEffectBuilder.trail(ObjectUtils.SPLITTABLE_RANDOM.nextBoolean());
            fireworkEffectBuilder.flicker(ObjectUtils.SPLITTABLE_RANDOM.nextBoolean());
            fireworkEffectBuilder.with(FireworkEffect.Type.values()[ObjectUtils.SPLITTABLE_RANDOM.nextInt(FireworkEffect.Type.values().length-1)]);

            fireworkMeta.addEffect(fireworkEffectBuilder.build());
            firework.setFireworkMeta(fireworkMeta);
        }
    }

}
