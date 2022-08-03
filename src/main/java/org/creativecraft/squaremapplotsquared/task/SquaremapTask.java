package org.creativecraft.squaremapplotsquared.task;

import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.creativecraft.squaremapplotsquared.SquaremapPlotSquared;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Rectangle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class SquaremapTask extends BukkitRunnable {
    private final UUID world;
    private final SimpleLayerProvider provider;
    private final SquaremapPlotSquared plugin;

    private boolean stop;

    public SquaremapTask(SquaremapPlotSquared plugin, UUID world, SimpleLayerProvider provider) {
        this.plugin = plugin;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }

        updatePlots();
    }

    /**
     * Update the plots.
     */
    private void updatePlots() {
        provider.clearMarkers();

        HashSet<Plot> plots = plugin
            .getPlotSquaredHook()
            .getPlots(this.world);

        if (plots == null || plots.isEmpty()) {
            return;
        }

        plots.forEach(this::handlePlot);
    }

    /**
     * Handle the plot markers.
     *
     * @param plot The plot.
     */
    private void handlePlot(Plot plot) {
        World world = plugin.getServer().getWorld(plot.getWorldName());
        List<String> tooltip = plugin.getSettings().getConfig().getStringList("settings.tooltip.plot");

        int i = 0;

        for (CuboidRegion region : plot.getRegions()) {
            Location min = new Location(world, region.getMinimumPoint().getX(), world.getMinHeight(), region.getMinimumPoint().getZ());
            Location max = new Location(world, region.getMaximumPoint().getX(), world.getMaxHeight(), region.getMaximumPoint().getZ());

            Rectangle rectangle = Marker.rectangle(
                Point.of(min.getBlockX(), min.getBlockZ()),
                Point.of(max.getBlockX() + 1, max.getBlockZ() + 1)
            );

            MarkerOptions.Builder options = MarkerOptions
                .builder()
                .strokeColor(plugin.getSettings().getStrokeColor())
                .strokeWeight(plugin.getSettings().getConfig().getInt("settings.style.stroke.weight"))
                .strokeOpacity(plugin.getSettings().getConfig().getDouble("settings.style.stroke.opacity"))
                .fillColor(plugin.getSettings().getFillColor())
                .fillOpacity(plugin.getSettings().getConfig().getDouble("settings.style.fill.opacity"))
                .clickTooltip(
                    String.join("", tooltip)
                        .replace("{id}", plugin.getPlotSquaredHook().getPlotId(plot))
                        .replace("{owner}", plugin.getPlotSquaredHook().getPlotOwner(plot))
                        .replace("{alias}", plugin.getPlotSquaredHook().getPlotAlias(plot))
                        .replace("{members}", plugin.getPlotSquaredHook().getPlotMembers(plot))
                        .replace("{trusted}", plugin.getPlotSquaredHook().getPlotTrusted(plot))
                        .replace("{rating}", plugin.getPlotSquaredHook().getPlotRating(plot))
                );

            rectangle.markerOptions(options);

            Key marker = Key.of(
                "plotsquared_" +
                world.getName() +
                "_plot_" +
                plugin.getPlotSquaredHook().getPlotSlug(plot, i)
            );

            provider.addMarker(marker, rectangle);

            i++;
        }
    }

    /**
     * Disable the task.
     */
    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
