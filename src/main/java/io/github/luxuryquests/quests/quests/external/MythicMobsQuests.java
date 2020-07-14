package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MythicMobsQuests extends ExternalQuestExecutor {

    public MythicMobsQuests(QuestsPlugin plugin) {
        super(plugin, "mythicmobs");
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getKiller();
        String mobName = event.getMobType().getInternalName();
        String mobEntityType = event.getMobType().getEntityType();
        String mobLevel = String.valueOf(event.getMobLevel());
        this.execute("kill_mob", player, result -> {
            result.subRoot("entityType", mobEntityType);
            result.subRoot("level", mobLevel);
            return result.root(mobName);
        });
    }
}
