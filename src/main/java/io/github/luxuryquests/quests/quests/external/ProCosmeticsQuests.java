package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import sv.file14.procosmetics.api.events.PlayerOpenTreasureEvent;
import sv.file14.procosmetics.api.events.PlayerPurchaseCosmeticEvent;
import sv.file14.procosmetics.api.events.PlayerPurchaseTreasureEvent;

public class ProCosmeticsQuests extends ExternalQuestExecutor {

    public ProCosmeticsQuests(QuestsPlugin plugin) {
        super(plugin, "procosmetics");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTreasurePurchase(PlayerPurchaseTreasureEvent event) {
        Player player = event.getPlayer();
        String treasureName = event.getTreasure().getName();
        int cost = event.getTreasure().getCost();

        this.execute("spend", player, cost, result -> result.root("treasure-chest"));
        this.execute("buy_treasure", player, result -> result.root(treasureName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerOpenTreasure(PlayerOpenTreasureEvent event) {
        Player player = event.getPlayer();
        String treasureName = event.getTreasure().getName();
        Location location = event.getLocation();

        this.execute("open_treasure", player, result -> {
            result.subRoot("x", String.valueOf(location.getBlock()));
            result.subRoot("y", String.valueOf(location.getBlockY()));
            result.subRoot("z", String.valueOf(location.getBlockZ()));
            return result.root(treasureName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onCosmeticPurchase(PlayerPurchaseCosmeticEvent event) {
        Player player = event.getPlayer();
        int cost = event.getCosmeticType().getCost();
        String cosmeticType = event.getCosmeticType().getName();
        String rarity = event.getCosmeticType().getCosmeticRarity().toString();

        this.execute("buy_cosmetic", player, result -> {
            result.subRoot("rarity", rarity);
            return result.root(cosmeticType);
        });
        this.execute("spend", player, cost, result -> result.root("cosmetic"));
    }
}
