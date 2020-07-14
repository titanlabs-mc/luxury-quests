package io.github.luxuryquests.quests.quests.internal;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageQuest extends QuestExecutor {

    public DamageQuest(QuestsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        int damage = (int) Math.round(event.getDamage());

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            if (CitizensAPI.getNPCRegistry().isNPC(player)) {
                return;
            }
        }
        this.execute("damage-player", player, damage, QuestResult::none, replacer -> replacer.set("damage", damage));
    }
}
