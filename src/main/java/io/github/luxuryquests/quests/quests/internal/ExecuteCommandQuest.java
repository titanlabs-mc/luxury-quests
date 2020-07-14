package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ExecuteCommandQuest extends QuestExecutor {

    public ExecuteCommandQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        this.execute("execute-command", player, result -> {
            result.subRoot("commandLower", command.toLowerCase());
            return result.root(command);
        }, replacer -> replacer.set("command", command));
    }
}
