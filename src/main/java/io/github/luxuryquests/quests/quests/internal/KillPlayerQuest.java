package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.QuestExecutor;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayerQuest extends QuestExecutor {

    public KillPlayerQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity();

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            if (CitizensAPI.getNPCRegistry().isNPC(killer) || CitizensAPI.getNPCRegistry().isNPC(victim)) {
                return;
            }
        }
        this.execute("kill-player", killer, result -> result.root(victim.getName()), replacer -> replacer.set("victim", victim.getName()));
    }
}
