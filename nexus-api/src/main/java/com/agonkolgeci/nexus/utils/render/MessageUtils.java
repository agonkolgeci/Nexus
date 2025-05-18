package com.agonkolgeci.nexus.utils.render;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class MessageUtils {

    public static final MiniMessage MM_SERIALIZER = MiniMessage.miniMessage();

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

}