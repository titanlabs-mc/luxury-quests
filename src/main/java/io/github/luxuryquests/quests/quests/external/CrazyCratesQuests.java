package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CrazyCratesQuests extends ExternalQuestExecutor {

    public CrazyCratesQuests(QuestsPlugin plugin) {
        super(plugin, "crazycrates");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPrize(PlayerPrizeEvent event) {
        Player player = event.getPlayer();
        String crateName = event.getCrateName();

        if (crateName == null) {
            return;
        }
        this.execute("open", player, result -> result.root(crateName));
    }
}
