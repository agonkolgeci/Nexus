package com.agonkolgeci.nexus.utils.render;

import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MessageUtils {

    public static final MiniMessage MM_SERIALIZER = MiniMessage.miniMessage();

    public static void sendMessage(@NotNull Type type, @NotNull Audience audience, @NotNull Component message) {
        audience.sendMessage(message.colorIfAbsent(NamedTextColor.GRAY));

        if(type.getSound() != null) {
            audience.playSound(type.getSound());
        }
    }

    public static void sendMessage(@NotNull Type type, @NotNull Stylizable stylizable, @NotNull Audience audience, @NotNull Component message) {
        sendMessage(type, audience, Component.text().append(stylizable.getPrefix().decorate(TextDecoration.BOLD)).appendSpace().append(Component.text("•", NamedTextColor.GRAY)).appendSpace().append(message).build());
    }

    public static void sendMessage(@NotNull Stylizable stylizable, @NotNull Audience audience, @NotNull Exception exception) {
        sendMessage(Type.ERROR, stylizable, audience, Component.text(ObjectUtils.requireNonNullElse(exception.getMessage(), "Une erreur inconnue s'est produite.."), NamedTextColor.RED));
    }

    @NotNull
    public static List<String> formatLore(@NotNull String text, int wordsPerLine) {
        @NotNull final List<String> lore = new ArrayList<>();
        @NotNull StringJoiner currentLine = new StringJoiner(" ");

        int count = 0;
        for (@NotNull final String word : text.split("\\s+")) {
            currentLine.add(word);
            count++;

            if(count == wordsPerLine) {
                lore.add(currentLine.toString().trim());

                currentLine = new StringJoiner(" ");
                count = 0;
            }
        }

        if(!currentLine.toString().trim().isEmpty()) {
            lore.add(currentLine.toString().trim());
        }

        return lore;
    }

    @Getter
    public enum Type {

        SUCCESS(Sound.sound(Key.key("note.pling"), Sound.Source.BLOCK, 1F, 2F)),
        INFO(null),
        ERROR(Sound.sound(Key.key("mob.villager.no"), Sound.Source.NEUTRAL, 1F, 1F));

        @Nullable private final Sound sound;
        Type(@Nullable Sound sound) {
            this.sound = sound;
        }
    }

    public interface Stylizable {

        @NotNull public abstract Component getPrefix();

    }

}