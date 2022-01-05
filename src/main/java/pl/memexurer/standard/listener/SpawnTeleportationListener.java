package pl.memexurer.standard.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.memexurer.standard.event.DuelEndEvent;

public class SpawnTeleportationListener implements Listener {
    @EventHandler
    private void onDuelEnd(DuelEndEvent event) {
        event.getVictim().teleport(event.getVictim().getWorld().getSpawnLocation());
    }

    @EventHandler
    private void onStart(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
    }
}
