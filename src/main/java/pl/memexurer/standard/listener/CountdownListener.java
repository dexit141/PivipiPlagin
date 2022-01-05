package pl.memexurer.standard.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.memexurer.standard.StandardPlugin;
import pl.memexurer.standard.event.DuelStartEvent;

public class CountdownListener implements Listener, Runnable {
    private static final long COUNTDOWN_TIME = 5000;
    private final Map<UUID, Long> countdownMap = new HashMap<>();

    public CountdownListener(StandardPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0L, 20L);
    }

    private static String convertTime(long time) {
        if (time <= 0L) {
            return " < 0s";
        }
        final StringBuilder stringBuilder = new StringBuilder();
        final long days = time / 86400000L;
        final long hours = time / 3600000L % 24L;
        final long minutes = time / 60000L % 60L;
        final long seconds = time / 1000L % 60L;
        final long milis = time % 1000L;
        if (days > 0L) {
            stringBuilder.append(days).append("d");
        }
        if (hours > 0L) {
            stringBuilder.append(hours).append("h");
        }
        if (minutes > 0L) {
            stringBuilder.append(minutes).append("min");
        }
        if (seconds > 0L) {
            stringBuilder.append(seconds).append("s");
        }
        if (days == 0L && hours == 0L && minutes == 0L && seconds == 0L && milis > 0L) {
            stringBuilder.append(milis).append("ms");
        }
        return stringBuilder.toString();
    }

    @EventHandler
    private void onDuelStart(DuelStartEvent event) {
        //nie mialem lepszego pomyslu na to, sory
        countdownMap.put(event.getReceiver().getUniqueId(), System.currentTimeMillis() + COUNTDOWN_TIME);
        countdownMap.put(event.getSender().getUniqueId(), System.currentTimeMillis() + COUNTDOWN_TIME);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player sender))
            return;
        if (!(event.getEntity() instanceof Player))
            return;

        if(!hasCountdownExpired(sender.getUniqueId()))
            event.setCancelled(true);
    }

    public boolean hasCountdownExpired(UUID player) {
        return System.currentTimeMillis() > countdownMap.getOrDefault(player, 0L);
    }

    public long getRemainingTime(UUID player) {
        return (countdownMap.get(player) - System.currentTimeMillis());
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!hasCountdownExpired(player.getUniqueId())) {
                long remainingTime = getRemainingTime(player.getUniqueId());
                if (remainingTime < 700) {
                    player.sendTitle("", "walcz!");
                } else {
                    player.sendTitle("", "Start za: " + convertTime(remainingTime));
                }
            }
        }
    }
}
