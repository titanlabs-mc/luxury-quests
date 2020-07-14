package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingQuest extends QuestExecutor {

    public FishingQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishCaught(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getCaught();
        PlayerFishEvent.State state = event.getState();

        if (state.equals(PlayerFishEvent.State.CAUGHT_FISH) && entity instanceof Item) {
            ItemStack itemStack = ((Item) entity).getItemStack();
            this.execute("fish", player, result -> result.root(itemStack), replacer -> replacer.set("caught", itemStack.getType()));
        }
    }
}
