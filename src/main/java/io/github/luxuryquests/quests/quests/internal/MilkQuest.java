package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MilkQuest extends QuestExecutor {

    public MilkQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMilk(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Cow) || !player.getItemInHand().getType().equals(MultiMaterial.BUCKET.getMaterial())) {
            return;
        }
        this.execute("milk", player, QuestResult::none);
    }
}
