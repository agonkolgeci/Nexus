package com.agonkolgeci.nexus.common.config;

import com.agonkolgeci.nexus.common.config.callback.any.ConfigurationObjectCallback;
import com.agonkolgeci.nexus.common.config.callback.type.ConfigurationTypeCallback;
import com.agonkolgeci.nexus.common.config.callback.type.ConfigurationTypeObjectCallback;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@Getter
@SuppressWarnings("unchecked")
public class ConfigSection extends AbstractAddon<ConfigManager> {

    @NotNull private final ConfigurationSection configuration;
    @Nullable private final ConfigSection parent;

    public ConfigSection(@NotNull ConfigManager controller, @NotNull ConfigurationSection configuration) {
        super(controller);

        this.configuration = configuration;
        this.parent = (configuration.getParent() != null ? new ConfigSection(controller, configuration.getParent()) : null);
    }

    @NotNull
    public String getName() {
        return configuration.getName();
    }

    @NotNull
    public String getPath() {
        return configuration.getCurrentPath();
    }

    @NotNull
    public ConfigSection of(@NotNull String... path) {
        return new ConfigSection(module, module.requireConfigurationSection(configuration, path));
    }

    @NotNull
    public Set<String> keys(boolean deep) {
        return configuration.getKeys(deep);
    }

    @NotNull
    public <O> List<O> keys(@NotNull ConfigurationObjectCallback<O> objectCallback, boolean deep) {
        @NotNull final List<O> objects = new ArrayList<>();

        for(@NotNull final String key : keys(deep)) {
            @NotNull final ConfigSection section = of(key);
            @NotNull final O object = objectCallback.retrieveObject(key, section);

            objects.add(object);
        }

        return objects;
    }

    @NotNull
    public <T, O> Map<T, O> keys(@NotNull ConfigurationTypeCallback<T> typeCallback, @NotNull ConfigurationTypeObjectCallback<T, O> objectCallback, boolean deep) {
        @NotNull final Map<T, O> objects = new HashMap<>();

        for(@NotNull final String key : keys(deep)) {
            @NotNull final ConfigSection section = of(key);

            @NotNull final T type = typeCallback.retrieveType(key, section);
            if(objects.containsKey(type)) {
                throw new IllegalStateException("Duplicated type key in keys !");
            }

            @NotNull final O object = objectCallback.retrieveObject(type, section);

            objects.put(type, object);
        }

        return objects;
    }

    public boolean has(@NotNull String key) {
        return get(key) != null;
    }

    @NotNull
    public <T> T set(@NotNull String key, @NotNull T object) {
        configuration.set(key, object);
        module.saveChanges();

        return object;
    }

    @NotNull
    public <T> T update(@NotNull T object) {
        ObjectUtils.requireNonNullElse(configuration.getRoot(), configuration).set(configuration.getName(), object);
        module.saveChanges();

        return object;
    }

    @Nullable
    public <T> T get(@NotNull String key) {
        return (T) configuration.get(key);
    }

    @NotNull
    public <T> T get(@NotNull String key, @NotNull T defaultValue) {
        return ObjectUtils.requireNonNullElse(get(key), defaultValue);
    }

    @NotNull
    public <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue) {
        return ObjectUtils.requireNonNullElseGet(get(key), defaultValue);
    }

    @NotNull
    public <T> T require(@NotNull String key) {
        return Objects.requireNonNull(get(key), String.format("An unknown error occurred while retrieving the '%s' key associated with the '%s' parent in the configuration.", key, configuration.getParent()));
    }

    @NotNull
    public <T> T require(@NotNull String key, @NotNull String message) {
        return Objects.requireNonNull(get(key), String.format("An unknown error occurred while retrieving the '%s' key associated with the '%s' parent in the configuration: '%s'", key, configuration.getParent(), message));
    }

}
