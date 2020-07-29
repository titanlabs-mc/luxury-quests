package io.github.luxuryquests.quests.quests.external;

import com.poompk.LobbyPresents.Presents.EventAPI.PlayerClickClaimedPresentEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LobbyPresentsPoompkQuests extends ExternalQuestExecutor {

    public LobbyPresentsPoompkQuests(QuestsPlugin plugin) {
        super(plugin, "lobbypresents");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPresentClaim(PlayerClickClaimedPresentEvent event) {
        Player player = event.getPlayer();
        String id = String.valueOf(event.getID());

        this.execute("find", player, result -> result.root(id), replacer -> replacer.set("id", id));
    }
}
