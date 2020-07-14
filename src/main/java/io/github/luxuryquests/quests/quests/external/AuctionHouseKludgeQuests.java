package io.github.luxuryquests.quests.quests.external;

import com.spawnchunk.auctionhouse.events.AuctionItemEvent;
import com.spawnchunk.auctionhouse.events.ItemAction;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AuctionHouseKludgeQuests extends ExternalQuestExecutor {

    public AuctionHouseKludgeQuests(QuestsPlugin plugin) {
        super(plugin, "auctionhouse_kludge");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAuctionItemInteract(AuctionItemEvent event) {
        Player buyer = event.getBidder().getPlayer();
        Player seller = event.getSeller().getPlayer();
        int price = Math.round(event.getPrice());
        ItemAction action = event.getItemAction();
        Material itemMaterial = event.getItem().getType();

        if (seller == null || action == null) {
            return;
        }
        switch (action) {
            case ITEM_LISTED:
                this.execute("list", seller, result -> {
                    result.subRoot("price", String.valueOf(price));
                    return result.root(itemMaterial.toString());
                });
                break;
            case ITEM_SOLD:
                if (buyer == null) {
                    return;
                }
                this.execute("sell", seller, result -> {
                    result.subRoot("buyer", buyer.getName());
                    return result.subRoot("item", itemMaterial.toString());
                });
                this.execute("buy", buyer, result -> {
                    result.subRoot("seller", seller.getName());
                    return result.root(itemMaterial.toString());
                });

                this.execute("profit", seller, price, result -> {
                    result.subRoot("buyer", buyer.getName());
                    return result.root(itemMaterial.toString());
                });

                this.execute("spend", buyer, price, result -> {
                    result.subRoot("seller", seller.getName());
                    return result.root(itemMaterial.toString());
                });
                break;
        }
    }
}
