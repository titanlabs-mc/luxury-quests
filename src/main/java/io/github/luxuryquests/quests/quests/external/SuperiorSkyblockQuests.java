package io.github.luxuryquests.quests.quests.external;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandJoinEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandUpgradeEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class SuperiorSkyblockQuests extends ExternalQuestExecutor {

    public SuperiorSkyblockQuests(QuestsPlugin plugin) {
        super(plugin, "superiorskyblock2");
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandCreate(IslandCreateEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());

        this.execute("create", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandJoin(IslandJoinEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());

        this.execute("join", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandUpgrade(IslandUpgradeEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        String upgrade = event.getUpgradeName();

        if (upgrade == null) {
            return;
        }
        this.execute("upgrade", player, result -> result.root(upgrade));
    }
}
