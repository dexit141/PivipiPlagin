package pl.memexurer.standard.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelInvitationEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private final Player receiver;
    private final Player sender;
    private boolean cancelled;

    public DuelInvitationEvent(Player receiver, Player sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Player getSender() {
        return sender;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
