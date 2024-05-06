package com.agonkolgeci.nexus.core.holograms;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HologramsManager extends PluginManager<NexusAPI> implements PluginAdapter {

    @NotNull private final List<Hologram> holograms;

    public HologramsManager(@NotNull NexusAPI instance) {
        super(instance);

        this.holograms = new ArrayList<>();
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void unload() {
        holograms.forEach(Hologram::remove);
    }

    @NotNull
    public Hologram register(@NotNull Hologram hologram) {
        holograms.add(hologram);
        return hologram;
    }

    @NotNull
    public Hologram create(@NotNull Location location, @NotNull Component title, @NotNull Hologram.Direction direction) {
        return register(new Hologram(this, location, title, direction));
    }

//    @NotNull
//    public Hologram createHologram(@NotNull Location location, @NotNull ItemStack itemStack, @NotNull Hologram.Direction direction) {
//        return registerHologram(new Hologram(module, location, itemStack, direction));
//    }

    public void delete(@NotNull Hologram hologram) {
        hologram.remove();
        holograms.remove(hologram);
    }
}
