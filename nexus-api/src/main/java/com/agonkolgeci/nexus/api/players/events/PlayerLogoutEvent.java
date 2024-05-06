package com.agonkolgeci.nexus.api.players.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerLogoutEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerLogoutEvent(@NotNull Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
