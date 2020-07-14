package io.github.luxuryquests.quests.quests.internal;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillMobQuest extends QuestExecutor {

    public KillMobQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        String stringEntity = entity.getType().toString().replace("Craft", "");
        int entityAmount = 1;
        if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
            entityAmount = WildStackerAPI.getEntityAmount(event.getEntity());
        }
        this.execute("kill-mob", player, entityAmount, result -> result.root(stringEntity), replacer -> replacer.set("entity", stringEntity));
    }
}
