package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import pl.plajer.buildbattle.api.event.game.BBGameEndEvent;
import pl.plajer.buildbattle.api.event.game.BBGameJoinEvent;
import pl.plajer.buildbattle.api.event.game.BBGameStartEvent;

public class BuildBattleTigerQuests extends ExternalQuestExecutor {

    public BuildBattleTigerQuests(QuestsPlugin plugin) {
        super(plugin, "buildbattle");
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameJoin(BBGameJoinEvent event) {
        Player player = event.getPlayer();
        String mapName = event.getArena().getMapName();

        this.execute("join", player, result -> result.root(mapName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameEnd(BBGameStartEvent event) {
        String mapName = event.getArena().getMapName();
        for (Player player : event.getArena().getPlayers()) {
            this.execute("play", player, result -> result.root(mapName));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameEnd(BBGameEndEvent event) {
        String mapName = event.getArena().getMapName();
        for (Player player : event.getArena().getPlayers()) {
            this.execute("finish", player, result -> result.root(mapName));
        }
    }
}
