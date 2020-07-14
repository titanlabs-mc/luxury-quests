package io.github.luxuryquests.quests.quests.external;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Relation;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class FactionsUuidQuests extends ExternalQuestExecutor {

    public FactionsUuidQuests(QuestsPlugin plugin) {
        super(plugin, "factions");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        FPlayers fPlayers = FPlayers.getInstance();
        Player victim = (Player) event.getEntity();
        Player killer = victim.getKiller();
        FPlayer factionKiller = fPlayers.getByPlayer(killer);
        FPlayer factionVictim = fPlayers.getByPlayer(victim);

        if (factionKiller.getRelationTo(factionVictim) == Relation.ENEMY) {
            this.execute("kill_enemy", killer, QuestResult::none);
        }
    }
}
