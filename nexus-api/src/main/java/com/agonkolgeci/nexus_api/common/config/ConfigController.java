package com.agonkolgeci.nexus_api.common.config;

import com.agonkolgeci.nexus_api.NexusPlugin;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import com.agonkolgeci.nexus_api.utils.objects.ObjectUtils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigController extends PluginModule<NexusPlugin> {

    @Getter @NotNull private final FileConfiguration configuration;

    public ConfigController(@NotNull NexusPlugin instance) {
        super(instance);

        this.plugin.saveDefaultConfig();
        this.configuration = plugin.getConfig();
    }

    public void saveChanges() {
        plugin.saveConfig();
    }

    public ConfigSection of() {
        return new ConfigSection(this, configuration);
    }

    @NotNull
    public ConfigSection of(@NotNull String... path) {
        return new ConfigSection(this, requireConfigurationSection(configuration, path));
    }

    @NotNull
    protected String translatePath(@NotNull String ... path) {
        return String.join(".", path);
    }

    @Nullable
    public ConfigurationSection retrieveConfigurationSection(@NotNull ConfigurationSection parent, @NotNull String... path) {
        return parent.getConfigurationSection(translatePath(path));
    }

    @NotNull
    public ConfigurationSection requireConfigurationSection(@NotNull ConfigurationSection parent, @NotNull String... path) {
        return ObjectUtils.requireNonNullElseGet(retrieveConfigurationSection(parent, translatePath(path)), () -> parent.createSection(translatePath(path)));
    }

}
