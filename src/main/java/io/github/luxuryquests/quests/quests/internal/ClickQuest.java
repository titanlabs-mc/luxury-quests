package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickQuest extends QuestExecutor {

    public ClickQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                this.execute("right-click", player, QuestResult::none);
                if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) {
                    return;
                }
                this.execute("right-click-block", player, result -> result.root(clickedBlock), replacer -> replacer.set("block", clickedBlock.getType()));
                break;
            case RIGHT_CLICK_AIR:
                this.execute("right-click", player, QuestResult::none);
                break;
            case LEFT_CLICK_AIR:
                this.execute("left-click", player, QuestResult::none);
                break;
            case LEFT_CLICK_BLOCK:
                this.execute("left-click", player, QuestResult::none);
                if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) {
                    return;
                }
                this.execute("left-click-block", player, result -> result.root(clickedBlock), replacer -> replacer.set("block", clickedBlock.getType()));
                break;
        }
    }
}
