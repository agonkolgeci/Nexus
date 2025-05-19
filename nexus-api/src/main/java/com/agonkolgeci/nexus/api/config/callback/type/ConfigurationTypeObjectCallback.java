package com.agonkolgeci.nexus.api.config.callback.type;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationTypeObjectCallback<T, O> {

    @NotNull O retrieveObject(@NotNull T type, @NotNull ConfigSection config);

}