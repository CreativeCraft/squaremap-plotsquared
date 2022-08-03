package org.creativecraft.squaremapplotsquared.hook;

import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.query.PlotQuery;
import org.bukkit.World;
import org.creativecraft.squaremapplotsquared.SquaremapPlotSquared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class PlotSquaredHook {
    private final SquaremapPlotSquared plugin;

    public PlotSquaredHook(SquaremapPlotSquared plugin) {
        this.plugin = plugin;
    }

    /**
     * Retrieve the claims.
     *
     * @param  uuid The world UUID.
     * @return HashSet
     */
    public HashSet<Plot> getPlots(UUID uuid) {
        World world = plugin.getServer().getWorld(uuid);

        if (
            world == null ||
            !plugin.getAreaManager().hasPlotArea(world.getName())
        ) {
            return null;
        }

        HashSet<Plot> plots = new HashSet<>();

        for (Plot plot : PlotQuery.newQuery().inWorld(world.getName())) {
            if (!plot.hasOwner()) {
                continue;
            }

            plots.add(plot);
        }

        return plots;
    }

    /**
     * Retrieve the plot ID.
     *
     * @param plot The plot.
     */
    public String getPlotId(Plot plot) {
        return plot.getId().toCommaSeparatedString();
    }

    /**
     * Retrieve the plot ID as a slug.
     *
     * @param plot  The plot.
     * @param value The iterator.
     */
    public String getPlotSlug(Plot plot, int value) {
        return plot.getId().toDashSeparatedString() + (value == 0 ? "" : "-" + value);
    }

    /**
     * Retrieve the plot owner.
     *
     * @param plot The plot.
     */
    public String getPlotOwner(Plot plot) {
        String name = plugin
            .getPlotSquared()
            .getImpromptuUUIDPipeline()
            .getSingle(plot.getOwnerAbs(), 20);

        if (name == null) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.unknown");
        }

        if (name.equals("*")) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.everyone");
        }

        return name;
    }

    /**
     * Retrieve the plot members.
     *
     * @param plot The plot.
     */
    public String getPlotMembers(Plot plot) {
        ArrayList<String> members = new ArrayList<>();

        plot.getMembers().forEach(member -> members.add(
            plugin.getPlotSquared().getImpromptuUUIDPipeline().getSingle(member, 20)
        ));

        if (members.isEmpty()) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.none");
        }

        return String.join(", ", members);
    }

    /**
     * Retrieve the plot trusted members.
     *
     * @param plot The plot.
     */
    public String getPlotTrusted(Plot plot) {
        ArrayList<String> trusted = new ArrayList<>();

        plot.getTrusted().forEach(member -> trusted.add(
            plugin.getPlotSquared().getImpromptuUUIDPipeline().getSingle(member, 20)
        ));

        if (trusted.isEmpty()) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.none");
        }

        return String.join(", ", trusted);
    }

    /**
     * Retrieve the plot rating.
     *
     * @param plot The plot.
     */
    public String getPlotRating(Plot plot) {
        double rating = plot.getAverageRating();

        if (Double.isNaN(rating)) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.unknown");
        }

        return Double.toString(rating);
    }

    /**
     * Retrieve the plot alias.
     *
     * @param plot The plot.
     */
    public String getPlotAlias(Plot plot) {
        String alias = plot.getAlias();

        return alias.isEmpty() ?
            plugin.getSettings().getConfig().getString("settings.tooltip.none") :
            alias;
    }
}
