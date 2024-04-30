package com.agonkolgeci.nexus_api.utils.render;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerUtils {

    public static final float DEFAULT_WALK_SPEED = 0.2F;
    public static final float DEFAULT_FLY_SPEED = 0.1F;

    public static final int POTION_EFFECT_MAX_AMPLIFIER = 127 - 1;

    public static void clearPlayer(@Nonnull Player player) {
        player.getInventory().clear();

        player.setHealth(20);
        player.setFoodLevel(20);

        player.setLevel(0);
        player.setExp(0);

        player.closeInventory();

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

}
