package com.agonkolgeci.nexus_proxy.plugin;

public interface PluginController<C extends PluginController<C>> {

    public abstract void load();
    public abstract void unload();

}
