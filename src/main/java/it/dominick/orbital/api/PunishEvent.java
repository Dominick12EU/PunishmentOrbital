package it.dominick.orbital.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.sql.Timestamp;

public class PunishEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private String action;
    private String reason;
    private String staffer;
    private String player;
    private Timestamp duration;

    public PunishEvent(String action, String reason, String staffer, String player, Timestamp duration) {
        this.action = action;
        this.reason = reason;
        this.staffer = staffer;
        this.player = player;
        this.duration = duration;
    }

    public String getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public String getStaffer() {
        return staffer;
    }

    public String getPlayer() {
        return player;
    }

    public Timestamp getDuration() {
        return duration;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
