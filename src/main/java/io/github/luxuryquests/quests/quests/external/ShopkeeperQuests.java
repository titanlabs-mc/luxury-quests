package io.github.luxuryquests.quests.quests.external;

import com.nisovin.shopkeepers.api.events.ShopkeeperOpenUIEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ShopkeeperQuests extends ExternalQuestExecutor {

    public ShopkeeperQuests(QuestsPlugin plugin) {
        super(plugin, "shopkeepers");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopkeeperTrade(ShopkeeperTradeEvent event) {
        Player player = event.getPlayer();
        Shopkeeper shopkeeper = event.getShopkeeper();
        ItemStack clickedItem = event.getClickEvent().getCurrentItem();
        ItemStack offeredItem1 = event.getOfferedItem1();
        ItemStack offeredItem2 = event.getOfferedItem2();
        String keeperId = shopkeeper.getIdString();
        String keeperUuid = shopkeeper.getUniqueId().toString();
        String keeperName = shopkeeper.getName();

        this.execute("trade", player, result -> {
            result.root(clickedItem);
            result.subRoot("keeper-id", keeperId);
            result.subRoot("keeper-uuid", keeperUuid);
            result.subRoot("keeper-name", keeperName);
            result.subRoot("first-offered-item", offeredItem1.getType().toString());
            result.subRoot("second-offered-item", offeredItem2.getType().toString());
            return result;
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopkeeperOpen(ShopkeeperOpenUIEvent event) {
        Player player = event.getPlayer();
        Shopkeeper shopkeeper = event.getShopkeeper();
        String keeperId = shopkeeper.getIdString();
        String keeperUuid = shopkeeper.getUniqueId().toString();
        String keeperName = shopkeeper.getName();

        this.execute("open", player, result -> {
            result.root(keeperId);
            result.subRoot("keeper-uuid", keeperUuid);
            result.subRoot("keeper-name", keeperName);
            return result;
        });
    }
}
