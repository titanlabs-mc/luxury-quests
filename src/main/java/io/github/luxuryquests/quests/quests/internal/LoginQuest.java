package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginQuest extends QuestExecutor {
    private final QuestsPlugin plugin;

    public LoginQuest(QuestsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> super.execute("login", player, QuestResult::none), 40);
    }
}
