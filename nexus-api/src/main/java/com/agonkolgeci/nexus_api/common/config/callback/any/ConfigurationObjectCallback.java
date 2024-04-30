package com.agonkolgeci.nexus_api.common.config.callback.any;

import com.agonkolgeci.nexus_api.common.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationObjectCallback<O> {

    @NotNull O retrieveObject(@NotNull String key, @NotNull ConfigSection config);

}