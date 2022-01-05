package pl.memexurer.standard.kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.memexurer.standard.event.DuelEndEvent;
import pl.memexurer.standard.event.DuelInvitationEvent;
import pl.memexurer.standard.event.DuelStartEvent;

import java.util.*;

public final class KitSelectionListener implements Listener {
    private static final ItemStack kitSelectItem = new ItemStack(Material.CHEST);

    static {
        ItemMeta meta = kitSelectItem.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Wybieranie zestawu");
        kitSelectItem.setItemMeta(meta);
    }

    private final Map<UUID, String> kitTypeMap = new HashMap<>();
    private final List<Kit> kits = new ArrayList<>();

    public KitSelectionListener() {
        kits.add(new Kit(new ItemStack[]{
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_HELMET)
        }, new ItemStack[]{
                new ItemStack(Material.DIAMOND_SWORD),
                new ItemStack(Material.GOLDEN_APPLE, 6),
        }, "Bambicki zestaw", Material.DIRT));

        kits.add(new Kit(new ItemStack[]{
                null,
                new ItemStack(Material.LEATHER_LEGGINGS),
                null,
                null
        }, new ItemStack[]{
                new ItemStack(Material.WOOD_SWORD)
        }, "Bambikowy zestaw", Material.SPONGE));
        //todo konfiguracja, ale po co to komu?
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof KitInventory) {
            ((KitInventory) event.getClickedInventory().getHolder()).handleClick(event);
        }
    }

    @EventHandler
    private void onGadgetClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
                event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        if (event.getItem() != null && event.getItem().isSimilar(kitSelectItem)) {
            event.getPlayer().openInventory(new KitInventory(event.getPlayer()).getInventory());
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        kitTypeMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        event.getPlayer().getInventory().clear();
        setKitSelectionItem(event.getPlayer());
    }

    private void setKitSelectionItem(HumanEntity player) {
        player.getInventory().setItem(4, kitSelectItem);
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onItemDrop(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onDuelInvitation(DuelInvitationEvent event) {
        String kitTypeReceiver = kitTypeMap.get(event.getReceiver().getUniqueId());
        String kitTypeSender = kitTypeMap.get(event.getSender().getUniqueId());

        if (kitTypeSender == null) {
            event.getSender().sendMessage("Aby zapraszac ludzi na duel, musisz najpierw wybrac swoj zestaw!");
            event.setCancelled(true);
            return;
        }

        if (kitTypeReceiver == null) {
            event.getSender().sendMessage("Gracz ktorego chcesz zaprosic na duel nie wybral jeszcze zestawu!");
            event.setCancelled(true);
            return;
        }

        if (!kitTypeReceiver.equals(kitTypeSender)) {
            event.getSender().sendMessage("Musisz miec taki sam zestaw jak gracz ktorego chcesz zaprosic aby z nim zagrac.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDuelStart(DuelStartEvent event) {
        event.getReceiver().getInventory().remove(kitSelectItem);
        event.getSender().getInventory().remove(kitSelectItem);
    }

    @EventHandler
    private void onDuelEnd(DuelEndEvent event) {
        clearInventory(event.getKiller());
        clearInventory(event.getVictim());
        setKitSelectionItem(event.getKiller());
        setKitSelectionItem(event.getVictim());
        kitTypeMap.remove(event.getKiller().getUniqueId());
        kitTypeMap.remove(event.getVictim().getUniqueId());
    }

    private static void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
    }

    public class KitInventory implements InventoryHolder {
        private final Player player;

        public KitInventory(Player player) {
            this.player = player;
        }

        @Override
        public Inventory getInventory() {
            Inventory inventory = Bukkit.createInventory(this, ((kits.size() / 9) + 1) * 9, "Kity");
            for (Kit kitEntry : kits) {
                ItemStack kitEntryItem = new ItemStack(kitEntry.getIcon());

                ItemMeta kitEntryItemMeta = kitEntryItem.getItemMeta();
                kitEntryItemMeta.setDisplayName(ChatColor.GRAY + kitEntry.getName());

                if (kitTypeMap.containsKey(player.getUniqueId()) && kitTypeMap.get(player.getUniqueId()).equals(kitEntry.getName())) {
                    //wiem ze to moge zrobic dodajac rejestrujac wlasny enchant, ale po co? XDDD
                    kitEntryItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    kitEntryItem.addUnsafeEnchantment(Enchantment.LUCK, 1);
                }

                kitEntryItem.setItemMeta(kitEntryItemMeta);
                inventory.addItem(kitEntryItem);
            }
            return inventory;
        }

        public void handleClick(InventoryClickEvent event) {
            if (event.getRawSlot() >= kits.size())
                return;

            Kit kit = kits.get(event.getRawSlot());
            kitTypeMap.put(event.getWhoClicked().getUniqueId(), kit.getName());
            kit.applyKit(event.getWhoClicked());
            setKitSelectionItem(event.getWhoClicked());
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage("Zmieniles swoj zestaw na " + kit.getName() + "!");
        }
    }
}
