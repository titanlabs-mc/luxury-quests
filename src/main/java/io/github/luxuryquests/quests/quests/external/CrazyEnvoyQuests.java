package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import me.badbones69.crazyenvoy.api.events.OpenEnvoyEvent;
import me.badbones69.crazyenvoy.api.events.UseFlareEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CrazyEnvoyQuests extends ExternalQuestExecutor {

    public CrazyEnvoyQuests(QuestsPlugin plugin) {
        super(plugin, "crazyenvoy");
    }

    @EventHandler(ignoreCancelled = true)
    public void onOpenEnvoy(OpenEnvoyEvent event) {
        Player player = event.getPlayer();
        String tierName = event.getTier().getName();

        if (tierName == null) {
            return;
        }
        this.execute("open_envoy", player, result -> result.root(tierName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseFlare(UseFlareEvent event) {
        Player player = event.getPlayer();

        this.execute("use_flare", player, QuestResult::none);
    }
}
