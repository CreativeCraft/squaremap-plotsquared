package org.creativecraft.squaremapplotsquared.hook;

import org.bukkit.World;
import org.creativecraft.squaremapplotsquared.SquaremapPlotSquared;
import org.creativecraft.squaremapplotsquared.task.SquaremapTask;
import xyz.jpenilla.squaremap.api.BukkitAdapter;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.SquaremapProvider;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SquaremapHook {
    private final Map<UUID, SquaremapTask> provider = new HashMap<>();
    private final SquaremapPlotSquared plugin;

    public SquaremapHook(SquaremapPlotSquared plugin) {
        this.plugin = plugin;
        hook();
    }

    /**
     * Hook into squaremap.
     */
    public void hook() {
        SquaremapProvider.get().mapWorlds().forEach(value -> {
            final World world = BukkitAdapter.bukkitWorld(value);

            SimpleLayerProvider provider = SimpleLayerProvider
                .builder(plugin.getSettings().getConfig().getString("settings.control.label", "PlotSquared"))
                .showControls(plugin.getSettings().getConfig().getBoolean("settings.control.show"))
                .defaultHidden(plugin.getSettings().getConfig().getBoolean("settings.control.hide"))
                .build();

            value.layerRegistry().register(Key.of("plotsquared_" + world.getUID()), provider);

            SquaremapTask task = new SquaremapTask(plugin, world.getUID(), provider);

            task.runTaskTimerAsynchronously(
                plugin,
                0,
                20L * plugin.getSettings().getConfig().getInt("settings.update-interval")
            );

            this.provider.put(world.getUID(), task);
        });
    }

    /**
     * Disable the squaremap hook.
     */
    public void disable() {
        provider.values().forEach(SquaremapTask::disable);
        provider.clear();
    }
}
