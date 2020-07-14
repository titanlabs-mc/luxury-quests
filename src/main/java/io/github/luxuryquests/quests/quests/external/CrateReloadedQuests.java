package io.github.luxuryquests.quests.quests.external;

import com.hazebyte.crate.api.event.RewardReceiveEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CrateReloadedQuests extends ExternalQuestExecutor {

    public CrateReloadedQuests(QuestsPlugin plugin) {
        super(plugin, "cratereloaded");
    }

    @EventHandler(ignoreCancelled = true)
    public void onRewardReceive(RewardReceiveEvent event) {
        Player player = event.getPlayer();
        String crateName = event.getCrate().getCrateName();

        this.execute("open", player, result -> result.root(crateName));
    }
}
