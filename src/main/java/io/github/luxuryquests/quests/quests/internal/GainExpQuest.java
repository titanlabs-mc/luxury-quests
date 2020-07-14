package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class GainExpQuest extends QuestExecutor {

    public GainExpQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int gainAmount = event.getAmount();
        if (gainAmount <= 0) {
            return;
        }

        this.execute("gain-experience", player, gainAmount, QuestResult::none, replacer -> replacer.set("amount", gainAmount));
    }
}
