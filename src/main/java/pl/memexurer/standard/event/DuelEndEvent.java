package pl.memexurer.standard.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelEndEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Player victim;
    private final Player killer;
    private final boolean hasQuit;

    public DuelEndEvent(Player victim, Player killer, boolean hasQuit) {
        this.victim = victim;
        this.killer = killer;
        this.hasQuit = hasQuit;
    }

    public Player getVictim() {
        return victim;
    }

    public Player getKiller() {
        return killer;
    }

    public boolean isHasQuit() {
        return hasQuit;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
