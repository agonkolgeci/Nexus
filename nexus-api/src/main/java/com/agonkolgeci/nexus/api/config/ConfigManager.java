package com.agonkolgeci.nexus.api.config;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigManager extends PluginManager<AbstractPlugin> {

    @Getter @NotNull private final FileConfiguration configuration;

    public ConfigManager(@NotNull AbstractPlugin instance) {
        super(instance);

        this.instance.getPlugin().saveDefaultConfig();
        this.configuration = instance.getPlugin().getConfig();
    }

    public void saveChanges() {
        instance.getPlugin().saveConfig();
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
