package io.github.luxuryquests.quests.quests.external;

import com.vk2gpz.tokenenchant.event.TEEnchantEvent;
import com.vk2gpz.tokenenchant.event.TETokenChangeEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TokenEnchantQuests extends ExternalQuestExecutor {

    public TokenEnchantQuests(QuestsPlugin plugin) {
        super(plugin, "tokenenchant");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTokensChange(TETokenChangeEvent event) {
        double gainedTokens = event.getNewTokenValue() - event.getOldTokenValue();
        if (gainedTokens < 1) {
            return;
        }
        Player player = event.getOfflinePlayer().getPlayer();
        this.execute("gain", player, (int) gainedTokens, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemEnchant(TEEnchantEvent event) {
        Player player = event.getPlayer();
        String enchantName = event.getCEHandler().getDisplayName();
        int amount = event.getNewLevel() - event.getOldLevel();
        this.execute("enchant", player, amount, result -> result.root(enchantName));
    }
}
