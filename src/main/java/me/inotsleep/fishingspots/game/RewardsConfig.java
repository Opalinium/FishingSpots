package me.inotsleep.fishingspots.game;

import me.inotsleep.utils.config.AbstractConfig;
import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.config.Path;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RewardsConfig extends AbstractConfig {
    @Path(value = "rarities")
    public Map<String, Map<String, Double>> rarities;

    @Path(value = "items")
    public Map<String, DropItems> items;

    public RewardsConfig(AbstractPlugin plugin) {
         super(plugin, "rewards.yml");
    }

    @Override
    public String getHeader() {
        return "Items will be autogenerated.\n" +
                "To generate you need to use command '/fishingspots createitem <id>' holding item in hand\n" +
                "After that just edit other settings here\n" +
                "\n" +
                "Rarity chances you need to create by yourself by this example:\n" +
                "rarities:\n" +
                "    common:\n" +
                "        item1: 10.0\n" +
                "        item2: 40.0\n" +
                "        item3: 100.0\n" +
                "\n" +
                "That means that you need to have 100% item, that will be most common";
    }

    @Override
    public void saveDefaults() {
        rarities = new HashMap<>();
        items = new HashMap<>();

        rarities.put("common", new HashMap<>());
        rarities.get("common").put("diamond", 10.0);
        rarities.get("common").put("fish", 100.0);

        rarities.put("uncommon", new HashMap<>());
        rarities.get("uncommon").put("diamond", 10.0);
        rarities.get("uncommon").put("fish", 100.0);

        rarities.put("rare", new HashMap<>());
        rarities.get("rare").put("diamond", 10.0);
        rarities.get("rare").put("fish", 100.0);

        rarities.put("epic", new HashMap<>());
        rarities.get("epic").put("diamond", 10.0);
        rarities.get("epic").put("fish", 100.0);

        rarities.put("legendary", new HashMap<>());
        rarities.get("legendary").put("diamond", 10.0);
        rarities.get("legendary").put("fish", 100.0);

        items.put("diamond", new DropItems(new ItemStack(Material.DIAMOND), 1, 3));
        items.put("fish", new DropItems(new ItemStack(Material.SALMON), 1, 1));
    }
}
