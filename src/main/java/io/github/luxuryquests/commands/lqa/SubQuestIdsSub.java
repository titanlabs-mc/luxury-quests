package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.SubQuest;
import org.bukkit.command.CommandSender;

public class SubQuestIdsSub extends LqSubCommand<CommandSender> {
    private final QuestCache questCache;

    public SubQuestIdsSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlats("subquest", "ids");
        this.addArgument(QuestType.class, "type");
        this.addArgument(String.class, "quest id");
        this.questCache = plugin.getQuestCache();
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        QuestType questType = this.parseArgument(args, 2);
        String id = this.parseArgument(args, 3);

        if (questType.equals(QuestType.UNKNOWN)) {
            this.lang.local("invalid-quest-type", args[2]).to(commandSender);
            return;
        }
        Quest quest = this.questCache.getQuest(questType, id);
        if (quest == null) {
            this.lang.local("invalid-quest-id", args[3], args[2].toLowerCase()).to(commandSender);
            return;
        }
        this.lang.local("subquest-ids-title").to(commandSender);
        for (SubQuest subQuest : quest.getSubQuests()) {
            this.lang.local("subquest-id", subQuest.getId(), subQuest.getName()).to(commandSender);
        }
    }
}
