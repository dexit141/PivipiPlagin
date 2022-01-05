package pl.memexurer.standard.kit;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Kit {
    private final ItemStack[] armor;
    private final ItemStack[] contents;
    private final String name;
    private final Material icon;

    public Kit(ItemStack[] armor, ItemStack[] contents, String name, Material icon) {
        this.armor = armor;
        this.contents = contents;
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public void applyKit(HumanEntity player) {
        player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(contents);
    }
}
