package com.agonkolgeci.nexus.utils.objects;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class ObjectUtils {

    public static final SplittableRandom SPLITTABLE_RANDOM = new SplittableRandom();

    @NotNull
    public static <O> O requireNonNullElse(@Nullable O object, @NotNull O defaultObject) {
        return object != null ? object : defaultObject;
    }

    @NotNull
    public static <O> O requireNonNullElseGet(@Nullable O object, @NotNull Supplier<O> defaultObject) {
        return object != null ? object : defaultObject.get();
    }

    public static <T> T retrieveRandomObject(@NotNull List<T> objects) {
        return objects.get(SPLITTABLE_RANDOM.nextInt(objects.size()));
    }

    public static <K, V> V retrieveObjectOrElseGet(@NotNull Map<K, V> map, @NotNull K key, @NotNull Supplier<V> supplier) {
        return requireNonNullElseGet(map.getOrDefault(key, null), () -> {
            @NotNull final V value = supplier.get();

            map.put(key, value);

            return value;
        });
    }

    @Nullable
    public static <E extends Enum<? extends E>> E fetchObject(@NotNull Class<E> anEnum, @Nullable String name) {
        return Arrays.stream(anEnum.getEnumConstants()).filter(object -> object.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @NotNull
    public static <E extends Enum<? extends E>> E fetchObject(@NotNull Class<E> anEnum, @Nullable String name, @NotNull E defaultObject) {
        return requireNonNullElse(fetchObject(anEnum, name), defaultObject);
    }

    @NotNull
    public static <E extends Enum<? extends E>> E fetchObject(@NotNull Class<E> anEnum, @Nullable String name, @NotNull String message) {
        return Objects.requireNonNull(fetchObject(anEnum, name), String.format(message, name));
    }

    @NotNull
    public static Color retrieveRandomColor() {
        return Color.fromBGR(SPLITTABLE_RANDOM.nextInt(255), SPLITTABLE_RANDOM.nextInt(255), SPLITTABLE_RANDOM.nextInt(255));
    }

    public static long toTicks(int seconds) {
        return seconds * 20L;
    }

    public static long toTicks(double seconds) {
        return Math.round(seconds * 20.0);
    }

}
