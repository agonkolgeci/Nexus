package com.agonkolgeci.nexus.plugin;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PluginScheduler {

    @Nullable BukkitTask getCurrentTask();

    @NotNull BukkitTask start();
    void stop();

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
