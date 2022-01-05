package pl.memexurer.standard.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.memexurer.standard.event.DuelEndEvent;
import pl.memexurer.standard.event.DuelStartEvent;

public final class PlayerHideListener implements Listener {
    private static void hideAllPlayers(Player receiver, Player exception) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (exception != player)
                receiver.hidePlayer(player);
    }

    private static void showAllPlayers(Player receiver, Player exception) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (exception != player)
                receiver.showPlayer(player);
    }

    @EventHandler
    private void onEnd(DuelEndEvent event) {
        showAllPlayers(event.getKiller(), event.getVictim());
        showAllPlayers(event.getVictim(), event.getKiller());
    }

    @EventHandler
    private void onEnd(DuelStartEvent event) {
        hideAllPlayers(event.getReceiver(), event.getSender());
        hideAllPlayers(event.getSender(), event.getReceiver());
    }
}
