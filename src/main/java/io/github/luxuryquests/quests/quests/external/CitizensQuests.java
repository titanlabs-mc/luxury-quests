package io.github.luxuryquests.quests.quests.external;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CitizensQuests extends ExternalQuestExecutor {

    public CitizensQuests(QuestsPlugin plugin) {
        super(plugin, "citizens");
    }

    @EventHandler(ignoreCancelled = true)
    public void onNPCRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();

        this.execute("click", player, result -> result.root(npc.getName()));
    }
}
