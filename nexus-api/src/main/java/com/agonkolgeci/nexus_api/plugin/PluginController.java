package com.agonkolgeci.nexus_api.plugin;

public interface PluginController {

    public abstract void load() throws Exception;
    public abstract void unload();

}
