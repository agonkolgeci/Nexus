package com.agonkolgeci.nexus.plugin;

public interface PluginAdapter {

    void load() throws Exception;
    void unload();

}
