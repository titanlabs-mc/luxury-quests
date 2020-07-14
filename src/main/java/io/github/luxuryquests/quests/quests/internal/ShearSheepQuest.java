package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearSheepQuest extends QuestExecutor {

    public ShearSheepQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        String entityName = event.getEntity().getCustomName();
        if (!(entity instanceof Sheep)) {
            return;
        }
        this.execute("shear", player, result -> result.root(entityName), replacer -> replacer.set("name", entityName));
    }
}
