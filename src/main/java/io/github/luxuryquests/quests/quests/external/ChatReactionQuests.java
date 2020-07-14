package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import me.clip.chatreaction.events.ReactionWinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChatReactionQuests extends ExternalQuestExecutor {

    public ChatReactionQuests(QuestsPlugin plugin) {
        super(plugin, "chatreaction");
    }

    @EventHandler(ignoreCancelled = true)
    public void onReactionWin(ReactionWinEvent event) {
        Player player = event.getWinner();

        this.execute("win", player, QuestResult::none);
    }
}
