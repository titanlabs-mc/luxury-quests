package io.github.luxuryquests.quests.quests.external;

import Clans.Events.ClanCreateEvent;
import Clans.Events.ClanJoinEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ClansQuests extends ExternalQuestExecutor {

    public ClansQuests(QuestsPlugin plugin) {
        super(plugin, "clans");
    }

    @EventHandler(ignoreCancelled = true)
    public void onClanCreate(ClanCreateEvent event) {
        Player player = event.getOwner();
        this.execute("create", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClanInvite(ClanJoinEvent event) {
        Player player = event.getPlayer();
        this.execute("join", player, QuestResult::none);
    }
}
