package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenerateQuest extends QuestExecutor {

    public RegenerateQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
        int gainAmount = (int) Math.round(event.getAmount());

        this.execute("regenerate", player, gainAmount, result -> result.root(regainReason.toString()), replacer -> replacer.set("reason", regainReason));
    }
}
