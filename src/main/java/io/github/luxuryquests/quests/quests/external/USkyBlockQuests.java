package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import us.talabrek.ultimateskyblock.api.event.InviteEvent;
import us.talabrek.ultimateskyblock.api.event.IslandChatEvent;
import us.talabrek.ultimateskyblock.api.event.MemberJoinedEvent;

public class USkyBlockQuests extends ExternalQuestExecutor {

    public USkyBlockQuests(QuestsPlugin plugin) {
        super(plugin, "uskyblock");
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandInvite(InviteEvent event) {
        Player inviter = event.getPlayer();
        Player invited = event.getGuest();

        this.execute("invite", inviter, QuestResult::none);
        this.execute("invited", invited, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandJoin(MemberJoinedEvent event) {
        Player player = event.getPlayerInfo().getPlayer();

        this.execute("join", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onIslandChat(IslandChatEvent event) {
        Player player = event.getPlayer();

        this.execute("island_chat", player, QuestResult::none);
    }
}
