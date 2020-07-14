package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementQuests extends QuestExecutor {

    public MovementQuests(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material blockAtLocation = player.getLocation().getBlock().getType();
        if ((event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) {
            return;
        }
        this.execute("move", player, QuestResult::none);
        if (player.isFlying()) {
            this.execute("fly", player, QuestResult::none);
            return;
        }
        this.execute("ground-move", player, QuestResult::none);
        this.execute(player.isSneaking() ? "sneak" : player.isSprinting() ? "sprint" : "walk", player, QuestResult::none);
        if (ServerVersion.isOver_V1_12()) {
            if (player.isGliding()) {
                this.execute("glide", player, QuestResult::none);
                return;
            }
        }
        if (blockAtLocation.toString().toLowerCase().contains("water")) {
            this.execute("swim", player, QuestResult::none);
        }
    }
}
