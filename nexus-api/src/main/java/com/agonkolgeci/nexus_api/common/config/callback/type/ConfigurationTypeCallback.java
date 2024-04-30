package com.agonkolgeci.nexus_api.common.config.callback.type;

import com.agonkolgeci.nexus_api.common.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationTypeCallback<K> {

    @NotNull K retrieveType(@NotNull String key, @NotNull ConfigSection config);

}