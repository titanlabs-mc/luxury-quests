package io.github.luxuryquests.quests.quests.external;

import com.vexsoftware.votifier.model.VotifierEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class VotifierQuests extends ExternalQuestExecutor {

    public VotifierQuests(QuestsPlugin plugin) {
        super(plugin, "votifier");
    }

    @EventHandler(ignoreCancelled = true)
    public void onVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());
        String serviceName = event.getVote().getServiceName();
        this.execute("vote", player, result -> result.root(serviceName));
    }
}
