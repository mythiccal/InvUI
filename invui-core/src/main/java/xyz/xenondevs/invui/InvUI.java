package xyz.xenondevs.invui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InvUI implements Listener {
    
    private static InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private Plugin plugin;
    
    private InvUI() {
    }
    
    public static @NotNull InvUI getInstance() {
        return instance == null ? instance = new InvUI() : instance;
    }
    
    public @NotNull Plugin getPlugin() {
        if (plugin == null)
            throw new IllegalStateException("Plugin is not set. Set it using InvUI.getInstance().setPlugin(plugin);");

        return plugin;
    }
    
    public void setPlugin(@Nullable Plugin plugin) {
        if (this.plugin != null)
            throw new IllegalStateException("Plugin is already set");
        
        if (plugin == null)
            return;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    
    public @NotNull Logger getLogger() {
        return getPlugin().getLogger();
    }
    
    public void addDisableHandler(@NotNull Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    @EventHandler
    private void handlePluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableHandlers.forEach(Runnable::run);
        }
    }
    
}
