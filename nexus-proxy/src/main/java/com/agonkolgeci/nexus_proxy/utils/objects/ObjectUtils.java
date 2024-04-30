package com.agonkolgeci.nexus_proxy.utils.objects;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Supplier;

public class ObjectUtils {

    @NotNull public static final SplittableRandom SPLITTABLE_RANDOM = new SplittableRandom();

    @Nonnull
    public static String randomShortUUID() {
        return Long.toString(ByteBuffer.wrap(UUID.randomUUID().toString().getBytes()).getLong(), Character.MAX_RADIX);
    }

    public static <K, V> V retrieveObjectOrElseGet(@NotNull Map<K, V> map, @NotNull K key, @NotNull Supplier<V> supplier) {
        return Objects.requireNonNullElseGet(map.getOrDefault(key, null), () -> {
            @NotNull final V value = supplier.get();

            map.put(key, value);

            return value;
        });
    }

    public static <T> T retrieveRandomObject(@NotNull List<T> objects) {
        return objects.get(SPLITTABLE_RANDOM.nextInt(objects.size()));
    }

    @Nullable
    public static <E extends Enum<? extends E>> E fetchObject(@NotNull Class<E> anEnum, @Nullable String name) {
        return Arrays.stream(anEnum.getEnumConstants()).filter(object -> object.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @NotNull
    public static <E extends Enum<? extends E>> E fetchObject(@NotNull Class<E> anEnum, @Nullable String name, @NotNull E defaultObject) {
        return Objects.requireNonNullElse(fetchObject(anEnum, name), defaultObject);
    }

    public static double retrieveTicks(int seconds) {
        return seconds * 20;
    }

    public static double retrieveSeconds(double ticks) {
        return ticks / 20;
    }


}
