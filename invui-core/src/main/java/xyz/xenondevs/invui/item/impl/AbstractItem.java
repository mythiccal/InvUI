package xyz.xenondevs.invui.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.util.Schedulers;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract implementation of the {@link Item} interface.
 */
public abstract class AbstractItem implements Item {
    
    private final Set<AbstractWindow> windows = new HashSet<>();
    
    @Override
    public void addWindow(AbstractWindow window) {
        synchronized (windows) {
            windows.add(window);
        }
    }
    
    @Override
    public void removeWindow(AbstractWindow window) {
        synchronized (windows) {
            windows.remove(window);
        }
    }
    
    @Override
    public Set<Window> getWindows() {
        synchronized (windows) {
            return Set.copyOf(windows);
        }
    }
    
    @Override
    public void notifyWindows() {
        for (AbstractWindow window : copyWindows()) {
            window.handleItemProviderUpdate(this);
        }
    }
    
    /**
     * Updates open windows. On a region-threaded server each inventory write is
     * scheduled onto that viewer's region thread.
     */
    protected void publishUpdate() {
        if (!Schedulers.isRegionThreaded()) {
            notifyWindows();
            return;
        }
        
        Plugin plugin = InvUI.getInstance().getPlugin();
        for (AbstractWindow window : copyWindows()) {
            Player viewer = window.getViewer();
            viewer.getScheduler().run(plugin, task -> window.handleItemProviderUpdate(this), null);
        }
    }
    
    private List<AbstractWindow> copyWindows() {
        synchronized (windows) {
            return new ArrayList<>(windows);
        }
    }
    
}
