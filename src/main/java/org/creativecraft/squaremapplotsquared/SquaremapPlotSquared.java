package org.creativecraft.squaremapplotsquared;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bstats.bukkit.MetricsLite;
import org.creativecraft.squaremapplotsquared.config.SettingsConfig;
import org.creativecraft.squaremapplotsquared.hook.PlotSquaredHook;
import org.creativecraft.squaremapplotsquared.hook.SquaremapHook;
import org.bukkit.plugin.java.JavaPlugin;

public class SquaremapPlotSquared extends JavaPlugin {
    public static SquaremapPlotSquared plugin;
    private SettingsConfig settingsConfig;
    private SquaremapHook squaremapHook;
    private PlotSquared plotSquared;
    private PlotSquaredHook plotSquaredHook;

    @Override
    public void onEnable() {
        plugin = this;
        plotSquared = PlotSquared.get();

        registerSettings();
        registerHooks();

        new MetricsLite(this, 16005);
    }

    @Override
    public void onDisable() {
        if (squaremapHook != null) {
            squaremapHook.disable();
        }
    }

    /**
     * Register the plugin hooks.
     */
    public void registerHooks() {
        squaremapHook = new SquaremapHook(this);
        plotSquaredHook = new PlotSquaredHook(this);
    }

    /**
     * Register the plugin config.
     */
    public void registerSettings() {
        settingsConfig = new SettingsConfig(this);
    }

    /**
     * Retrieve the plugin config.
     *
     * @return SettingsConfig
     */
    public SettingsConfig getSettings() {
        return settingsConfig;
    }

    /**
     * Retrieve the PlotSquared instance.
     *
     * @return PlotSquared
     */
    public PlotSquared getPlotSquared() {
        return plotSquared;
    }

    /**
     * Retrieve the PlotSquared Area Manager instance.
     *
     * @return PlotAreaManager
     */
    public PlotAreaManager getAreaManager() {
        return plotSquared.getPlotAreaManager();
    }

    /**
     * Retrieve the CrashClaim hook instance.
     *
     * @return plotSquaredHook
     */
    public PlotSquaredHook getPlotSquaredHook() {
        return plotSquaredHook;
    }
}
