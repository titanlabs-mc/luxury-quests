package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatQuest extends QuestExecutor {
    private final SimplePlugin plugin;

    public ChatQuest(QuestsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        this.plugin.runSync(() -> {
            Player player = event.getPlayer();
            String message = event.getMessage();

            this.execute("chat-stripped", player, result -> result.root(Text.decolorize(message)), replacer -> replacer.set("message", message));
            this.execute("chat", player, result -> result.root(message).subRoot("messageLower", message.toLowerCase()), replacer -> replacer.set("message", message));
        });
    }
}
