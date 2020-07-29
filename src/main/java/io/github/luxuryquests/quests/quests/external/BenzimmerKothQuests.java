package io.github.luxuryquests.quests.quests.external;

import com.benzimmer123.koth.events.KothLoseCapEvent;
import com.benzimmer123.koth.events.KothWinEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BenzimmerKothQuests extends ExternalQuestExecutor {

    public BenzimmerKothQuests(QuestsPlugin plugin) {
        super(plugin, "koth");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCapture(KothWinEvent event) {
        Player player = event.getCapper();
        int capTime = event.getCaptureTime();
        String kothName = event.getKOTH().getName();

        this.execute("capture", player, capTime, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
        this.execute("win_cap", player, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothEnd(KothLoseCapEvent event) {
        Player player = event.getCapper();
        int capTime = event.getCaptureTime();
        String kothName = event.getKOTH().getName();

        this.execute("capture", player, capTime, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }
}
