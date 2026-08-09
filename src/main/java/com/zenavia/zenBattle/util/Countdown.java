package com.zenavia.zenBattle.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Countdown {
    // tâche de compte à rebours réutilisable
    private final Plugin plugin;
    private int secondsLeft;
    private final Runnable onTick;
    private final Runnable onFinish;
    private BukkitTask task;

    public Countdown(Plugin plugin, int seconds, Runnable onTick, Runnable onFinish) {
        this.plugin = plugin;
        this.secondsLeft = seconds;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    onFinish.run();
                    cancel();
                    return;
                }
                onTick.run();
                secondsLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancel() {
        if (task != null) task.cancel();
    }
}
