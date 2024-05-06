package com.agonkolgeci.nexus.utils.storage;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@SuppressWarnings("unchecked")
public class EncoderUtils {

    @Nullable
    public static String encodeObject(@NotNull Object object) {
        try {
            @NotNull final ByteArrayOutputStream io = new ByteArrayOutputStream();
            @NotNull final BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

            os.writeObject(object);
            os.flush();

            return Base64.getEncoder().encodeToString(io.toByteArray());
        } catch (Exception exception) {
            return null;
        }
    }

    @Nullable
    public static <T> T decodeBase64(@NotNull String source) {
        try {
            @NotNull final ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(source));
            @NotNull final BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            return (T) is.readObject();
        } catch (Exception exception) {
            return null;
        }
    }

}
