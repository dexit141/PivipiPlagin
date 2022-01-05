package pl.memexurer.standard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.standard.event.DuelEndEvent;
import pl.memexurer.standard.event.DuelStartEvent;
import pl.memexurer.standard.kit.KitSelectionListener;
import pl.memexurer.standard.listener.CountdownListener;
import pl.memexurer.standard.listener.DuelInvitationListener;
import pl.memexurer.standard.listener.PlayerHideListener;
import pl.memexurer.standard.listener.SpawnTeleportationListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StandardPlugin extends JavaPlugin implements Listener {
    private final Map<UUID, UUID> combatPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerHideListener(), this);
        getServer().getPluginManager().registerEvents(new DuelInvitationListener(this), this);
        getServer().getPluginManager().registerEvents(new KitSelectionListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnTeleportationListener(), this);
        getServer().getPluginManager().registerEvents(new CountdownListener(this), this);
    }

    /*
    podstawowa mechanika here
     */
    @EventHandler
    private void onCombat(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player sender))
            return;
        if (!(event.getEntity() instanceof Player receiver))
            return;
        if (!isInCombat(sender) || !isInCombat(receiver))
            event.setCancelled(true);
    }

    @EventHandler
    private void onCombatDeath(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player sender))
            return;
        if (!(event.getEntity() instanceof Player receiver))
            return;

        if (combatPlayers.containsValue(receiver.getUniqueId())) {
            if (receiver.getHealth() - event.getDamage() <= 0.0d) {
                event.setCancelled(true);
                receiver.setHealth(receiver.getMaxHealth());
                sender.setHealth(sender.getMaxHealth());
                endCombat(sender, receiver);
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (combatPlayers.containsValue(event.getPlayer().getUniqueId())) {
            event.getPlayer().damage(event.getPlayer().getHealth());
            endCombat(Bukkit.getPlayer(combatPlayers.get(event.getPlayer().getUniqueId())), event.getPlayer(), true);
        }
    }

    @EventHandler
    private void onBuild(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBuild(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    private void mobSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onEat(PlayerItemConsumeEvent event) {
        if (!isInCombat(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    /*
    API jakies
     */
    private void endCombat(Player winner, Player loser, boolean quit) {
        getServer().getPluginManager().callEvent(new DuelEndEvent(loser, winner, quit));
        combatPlayers.remove(winner.getUniqueId());
        combatPlayers.remove(loser.getUniqueId());
    }

    public void endCombat(Player winner, Player loser) {
        this.endCombat(winner, loser, false);
    }

    public boolean isInCombat(Player player) {
        return combatPlayers.containsValue(player.getUniqueId());
    }

    public void startCombat(Player player1, Player player2) {
        DuelStartEvent startEvent = new DuelStartEvent(player1, player2);
        getServer().getPluginManager().callEvent(startEvent);
        if (startEvent.isCancelled())
            return;
        combatPlayers.put(player1.getUniqueId(), player2.getUniqueId());
        combatPlayers.put(player2.getUniqueId(), player1.getUniqueId());
    }
}
