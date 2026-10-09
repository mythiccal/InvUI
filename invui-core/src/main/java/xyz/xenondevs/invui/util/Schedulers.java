package xyz.xenondevs.invui.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;

/**
 * Runs tasks on the Bukkit scheduler on Paper, and on the region schedulers on Folia-based servers.
 * <p>
 * Tasks that touch a player or their inventory must use {@link #runFor(Player, Runnable)} or pass that
 * player to {@link #runRepeating(Runnable, long, Player)}. A repeating task with no player is global:
 * it must not read or write player state itself.
 */
public final class Schedulers {
    
    private static final boolean REGION_THREADED = detectRegionThreading();
    
    private Schedulers() {}
    
    public static boolean isRegionThreaded() {
        return REGION_THREADED;
    }
    
    /**
     * Runs {@code task} on the next tick of the player's region. On Paper this is the main thread.
     */
    public static void runFor(@NotNull Player player, @NotNull Runnable task) {
        Plugin plugin = plugin();
        if (!REGION_THREADED) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }
        player.getScheduler().run(plugin, scheduled -> task.run(), null);
    }
    
    /**
     * Runs {@code task} on the next global tick. On a region-threaded server the task must not
     * touch players, entities, or worlds.
     */
    public static void runNextTick(@NotNull Runnable task) {
        Plugin plugin = plugin();
        if (!REGION_THREADED) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }
        Bukkit.getGlobalRegionScheduler().run(plugin, scheduled -> task.run());
    }
    
    public static void runAsync(@NotNull Runnable task) {
        Plugin plugin = plugin();
        if (!REGION_THREADED) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, scheduled -> task.run());
    }
    
    public static @NotNull RepeatingTask runRepeating(@NotNull Runnable task, long periodTicks) {
        return runRepeating(task, periodTicks, null);
    }
    
    /**
     * Repeats {@code task} every {@code periodTicks}. When {@code regionOwner} is set on a
     * region-threaded server, the task runs on that player's region thread.
     */
    public static @NotNull RepeatingTask runRepeating(@NotNull Runnable task, long periodTicks, @Nullable Player regionOwner) {
        Plugin plugin = plugin();
        if (REGION_THREADED && regionOwner != null) {
            ScheduledTask scheduled = regionOwner.getScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), null, 1L, periodTicks);
            if (scheduled == null)
                return new RepeatingTask(() -> {});
            return new RepeatingTask(scheduled::cancel);
        }
        
        if (!REGION_THREADED) {
            return new RepeatingTask(Bukkit.getScheduler().runTaskTimer(plugin, task, 0L, periodTicks)::cancel);
        }
        
        return new RepeatingTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduled -> task.run(), 1L, periodTicks)::cancel);
    }
    
    private static @NotNull Plugin plugin() {
        return InvUI.getInstance().getPlugin();
    }
    
    private static boolean detectRegionThreading() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static final class RepeatingTask {
        
        private final Runnable cancelCallback;
        
        RepeatingTask(@NotNull Runnable cancelCallback) {
            this.cancelCallback = cancelCallback;
        }
        
        public void cancel() {
            cancelCallback.run();
        }
        
    }
    
}
