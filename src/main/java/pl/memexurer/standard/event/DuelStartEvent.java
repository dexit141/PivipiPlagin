package pl.memexurer.standard.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelStartEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player sender;
    private final Player receiver;
    private boolean cancelled;

    public DuelStartEvent(Player sender, Player receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
