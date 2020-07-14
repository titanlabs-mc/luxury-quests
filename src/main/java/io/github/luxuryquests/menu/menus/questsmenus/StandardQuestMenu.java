package io.github.luxuryquests.menu.menus.questsmenus;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.entity.Player;

import java.util.Collection;

public class StandardQuestMenu extends AbstractQuestMenu {
    private final QuestType questType;

    public StandardQuestMenu(QuestsPlugin plugin, Config config, Player player, QuestType questType) {
        super(plugin, config, player);
        this.questType = questType;
    }

    @Override
    public Collection<Quest> quests() {
        return this.plugin.getQuestCache().getQuests(this.questType).values();
    }
}
