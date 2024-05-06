package com.agonkolgeci.nexus.plugin;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractAddon<M> {

    @NotNull protected final M module;

    public AbstractAddon(@NotNull M module) {
        this.module = module;
    }
}
