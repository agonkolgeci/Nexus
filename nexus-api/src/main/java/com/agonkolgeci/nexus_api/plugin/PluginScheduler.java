package com.agonkolgeci.nexus_api.plugin;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PluginScheduler {

    @Nullable public abstract BukkitTask getCurrentTask();

    @NotNull public abstract BukkitTask start();
    public abstract void stop();

    default boolean isRunning() {
        try {
            return (this.getCurrentTask() != null);
        } catch (Exception exception) {
            return false;
        }
    }

    class TaskRunningException extends RuntimeException {
        public TaskRunningException() {
            super("This task is already running.");
        }
    }

    class TaskNotRunningException extends RuntimeException {
        public TaskNotRunningException() {
            super("This task is not running.");
        }
    }

}
