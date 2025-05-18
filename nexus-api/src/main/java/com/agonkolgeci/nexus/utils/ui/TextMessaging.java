package com.agonkolgeci.nexus.utils.ui;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@Getter
public class TextMessaging {

    private final Component label;

    public TextMessaging(String label, TextColor color) {
        this.label = Component.text(label, color, TextDecoration.BOLD);
    }

    public static @NotNull Component custom(Component label, Component message) {
        return Component.empty()
                .append(label.colorIfAbsent(NamedTextColor.GOLD))
                .appendSpace()
                .append(Component.text("•", NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(message.colorIfAbsent(NamedTextColor.GRAY));
    }

    public @NotNull Component info(Component message) {
        return custom(label, message);
    }

    public Component success(Component text) {
        return info(text.colorIfAbsent(NamedTextColor.GREEN));
    }

    public Component success(String message) {
        return success(Component.text(message));
    }

    public Component error(Component text) {
        return info(text.colorIfAbsent(NamedTextColor.RED));
    }

    public Component error(String message) {
        return error(Component.text(message));
    }

    public Component error(RuntimeException exception) {
        return error(Component.text(exception.getMessage()));
    }

}