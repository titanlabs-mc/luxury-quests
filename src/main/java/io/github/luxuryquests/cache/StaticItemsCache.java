package io.github.luxuryquests.cache;

import io.github.luxuryquests.QuestsPlugin;
import me.hyfe.simplespigot.cache.SimpleCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import org.bukkit.inventory.ItemStack;

public class StaticItemsCache extends SimpleCache<String, ItemStack> {
    private final QuestsPlugin plugin;

    public StaticItemsCache(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void cache() {
        Config config = this.plugin.getConfig("static-items");
        for (String key : config.keys("", false)) {
            if (!config.bool(key.concat(".enabled"))) {
                continue;
            }
            this.set(key, SpigotItem.toItem(config, key.concat(".item")));
        }
    }
}
