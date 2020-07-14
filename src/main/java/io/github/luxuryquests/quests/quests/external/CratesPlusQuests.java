package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import plus.crates.Events.CrateOpenEvent;

public class CratesPlusQuests extends ExternalQuestExecutor {

    public CratesPlusQuests(QuestsPlugin plugin) {
        super(plugin, "cratesplus");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        String crateName = event.getCrate().getName();
        Location location = event.getBlockLocation();
        if (crateName == null || location == null) {
            return;
        }
        this.execute("open", player, result -> {
            result.subRoot("x", String.valueOf(location.getBlockX()));
            result.subRoot("y", String.valueOf(location.getBlockY()));
            result.subRoot("z", String.valueOf(location.getBlockZ()));
            return result.root(crateName);
        });
    }
}
