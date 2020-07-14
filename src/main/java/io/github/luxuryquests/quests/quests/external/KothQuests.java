package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CappingPlayer;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;

public class KothQuests extends ExternalQuestExecutor {

    public KothQuests(QuestsPlugin plugin) {
        super(plugin, "koth");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothEnd(KothEndEvent event) {
        String kothName = event.getRunningKoth().getKoth().getName();
        Capper<?> capper = event.getWinner();
        if (!(capper instanceof CappingPlayer)) {
            return;
        }
        Player player = Bukkit.getPlayer(((OfflinePlayer) capper.getObject()).getUniqueId());
        this.execute("win_cap", player, result -> result.root(kothName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCap(KothLeftEvent event) {
        String kothName = event.getRunningKoth().getKoth().getName();
        Capper<?> capper = event.getCapper();
        if (!(capper instanceof CappingPlayer)) {
            return;
        }
        Player player = Bukkit.getPlayer(((OfflinePlayer) capper.getObject()).getUniqueId());
        int timeCaptured = event.getAmountSecondsCapped();
        this.execute("capture", player, timeCaptured, result -> result.root(kothName));
    }
}
