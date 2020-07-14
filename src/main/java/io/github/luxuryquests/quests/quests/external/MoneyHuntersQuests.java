package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.moneyhunters.api.events.PlayerJobExpGainEvent;
import su.nightexpress.moneyhunters.api.events.PlayerJobLevelUpEvent;

public class MoneyHuntersQuests extends ExternalQuestExecutor {

    public MoneyHuntersQuests(QuestsPlugin plugin) {
        super(plugin, "moneyhunters");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(PlayerJobLevelUpEvent event) {
        Player player = event.getPlayer();
        String jobName = event.getJob().getName();
        int level = event.getNewLevel();

        this.execute("level_up", player, result -> {
            result.subRoot("level", String.valueOf(level));
            return result.root(jobName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onExperienceGain(PlayerJobExpGainEvent event) {
        Player player = event.getPlayer();
        String jobName = event.getJob().getName();

        this.execute("gain_exp", player, result -> result.root(jobName));
    }
}
