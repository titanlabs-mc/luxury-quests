package io.github.luxuryquests.quests.quests.external;

import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.WarpCreateEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ASkyblockQuests extends ExternalQuestExecutor {

    public ASkyblockQuests(QuestsPlugin plugin) {
        super(plugin, "askyblock");
    }

    @EventHandler(ignoreCancelled = true)
    public void onNewIsland(IslandNewEvent event) {
        Player player = event.getPlayer();
        String schematicName = event.getSchematicName().getName();
        if (schematicName == null) {
            return;
        }
        this.execute("create_island", player, result -> result.root(schematicName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onWarpCreate(WarpCreateEvent event) {
        Player player = Bukkit.getPlayer(event.getCreator());

        this.execute("warp", player, QuestResult::none);
    }
}
