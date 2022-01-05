package pl.memexurer.standard.listener;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import pl.memexurer.standard.StandardPlugin;
import pl.memexurer.standard.event.DuelInvitationEvent;

public final class DuelInvitationListener implements Listener {
    private static final long DELAY_TIME = 100;

    private final StandardPlugin plugin;
    private final Multimap<UUID, UUID> invites = MultimapBuilder
            .hashKeys()
            .arrayListValues().build();
    private final Map<UUID, Long> delayMap = new HashMap<>();

    public DuelInvitationListener(StandardPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean doDelay(UUID player) {
        if(delayMap.containsKey(player)) {
            long diff = delayMap.get(player) - System.currentTimeMillis();
            if(diff > 0) {
                return true;
            } else {
                delayMap.put(player, System.currentTimeMillis() + DELAY_TIME);
                return false;
            }
        }

        delayMap.put(player, System.currentTimeMillis() + DELAY_TIME);
        return false;
    }

    private void invitePlayer(Player receiver, Player sender) {
        if (plugin.isInCombat(sender) || plugin.isInCombat(receiver))
            return;

        if(doDelay(sender.getUniqueId())) {
            return;
        }

        DuelInvitationEvent event = new DuelInvitationEvent(receiver, sender);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (invites.containsEntry(sender.getUniqueId(), receiver.getUniqueId())) {
            invites.remove(sender.getUniqueId(), receiver.getUniqueId());
            sender.sendMessage("Zaakceptowales zaproszenie od gracza " + receiver.getName());
            receiver.sendMessage("Gracz " + sender.getName() + " zaakceptowal zaproszenie!");
            plugin.startCombat(sender, receiver);
            return;
        } else if (invites.containsEntry(receiver.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage("Juz zaprosiles tego gracza!");
            return;
        }

        invites.put(receiver.getUniqueId(), sender.getUniqueId());
        sender.sendMessage("Zaprosiles gracza " + receiver.getName() + " na duel!");
        receiver.sendMessage("Otrzymales zaproszenie od gracza " + sender.getName() + " na duel!");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onCombat(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player sender))
            return;
        if (!(event.getEntity() instanceof Player receiver))
            return;
        invitePlayer(receiver, sender);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onCombat(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player receiver))
            return;
        invitePlayer(receiver, event.getPlayer());
    }
}
