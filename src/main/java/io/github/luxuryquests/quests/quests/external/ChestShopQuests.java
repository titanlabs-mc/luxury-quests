package io.github.luxuryquests.quests.quests.external;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChestShopQuests extends ExternalQuestExecutor {

    public ChestShopQuests(QuestsPlugin plugin) {
        super(plugin, "chestshop");
    }

    @EventHandler(ignoreCancelled = true)
    public void afterShopCreate(ShopCreatedEvent event) {
        Player player = event.getPlayer();

        this.execute("create", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(TransactionEvent event) {
        Player buyer = event.getClient();
        Player seller = Bukkit.getPlayer(event.getOwnerAccount().getUuid());
        int spentAmount = event.getExactPrice().intValue();

        if (buyer == null || seller == null) {
            return;
        }
        this.execute("buy", buyer, result -> result.root(seller.getName()));
        this.execute("sell", seller, result -> result.root(buyer.getName()));
        this.execute("spend", buyer, spentAmount, result -> result.root(seller.getName()));
        this.execute("profit", seller, spentAmount, result -> result.root(buyer.getName()));
    }

    @EventHandler(ignoreCancelled = true)
    public void afterShopCreate(ShopDestroyedEvent event) {
        Player player = event.getDestroyer();

        this.execute("destroy", player, QuestResult::none);
    }
}
