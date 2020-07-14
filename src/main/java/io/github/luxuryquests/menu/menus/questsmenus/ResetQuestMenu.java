package io.github.luxuryquests.menu.menus.questsmenus;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.Lockable;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.ResetType;
import io.github.luxuryquests.quests.workers.QuestReset;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ResetQuestMenu extends AbstractQuestMenu {
    private final QuestReset questReset;

    public ResetQuestMenu(QuestsPlugin plugin, Config config, Player player, ResetType resetType) {
        super(plugin, config, player);
        this.questReset = resetType.getQuestReset();
    }

    @Override
    public void redraw() {
        this.drawPageableItems(() -> this.drawConfigItems(replacer -> replacer.set("time_left", this.questReset.asString())));
    }

    @Override
    public Collection<Quest> quests() {
        return this.questReset.getCurrentQuests();
    }
}
