package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceQuest extends QuestExecutor {

    public BlockPlaceQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        super.execute("block-place", player, result -> result.root(block), replacer -> replacer.set("block", block.getType()));
    }
}
