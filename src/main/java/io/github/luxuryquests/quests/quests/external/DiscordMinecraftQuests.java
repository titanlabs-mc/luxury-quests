package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import me.enderaura.dcmc.api.event.AccountLinkedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DiscordMinecraftQuests extends ExternalQuestExecutor {

    public DiscordMinecraftQuests(QuestsPlugin plugin) {
        super(plugin, "discordminecraft");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAccountLink(AccountLinkedEvent event) {
        Player player = event.getPlayer().getPlayer();

        this.execute("link", player, QuestResult::none);
    }
}
