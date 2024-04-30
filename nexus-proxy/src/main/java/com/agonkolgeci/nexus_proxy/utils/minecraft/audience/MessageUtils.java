package com.agonkolgeci.nexus_proxy.utils.minecraft.audience;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MessageUtils {

    public static final Sound SOUND_SUCCESS = Sound.sound(Key.key("minecraft:block.note_block.pling"), Sound.Source.AMBIENT, 1F, 2F);
    public static final Sound SOUND_ERROR = Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.AMBIENT, 1F, 1F);

    public static void sendSuccess(@Nonnull Audience audience, @Nonnull Component component, @Nonnull Sound sound) {
        audience.sendMessage(component.colorIfAbsent(NamedTextColor.GRAY));
        audience.playSound(sound, Sound.Emitter.self());
    }

    public static void sendSuccess(@Nonnull Audience audience, @Nonnull Component component) {
        sendSuccess(audience, component, SOUND_SUCCESS);
    }

    public static void showTitle(@Nonnull Audience audience, @Nonnull Title title) {
        audience.showTitle(title);
    }

    public static void showBossBar(@Nonnull Audience audience, @Nonnull BossBar bossBar) {
        audience.showBossBar(bossBar);
    }

    public static void sendActionBar(@Nonnull Audience audience, @Nonnull ComponentLike text) {
        audience.sendActionBar(text);
    }

    public static void sendError(@Nonnull Audience audience, @Nonnull Exception exception, @Nonnull Sound sound) {
        audience.sendMessage(Component.text(Objects.requireNonNullElse(exception.getMessage(), "Error"), NamedTextColor.RED));
        audience.playSound(sound, Sound.Emitter.self());
    }

    public static void sendError(@Nonnull Audience audience, @Nonnull Exception exception) {
        sendError(audience, exception, SOUND_ERROR);
    }
}