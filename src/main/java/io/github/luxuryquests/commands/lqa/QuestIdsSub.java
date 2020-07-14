package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class QuestIdsSub extends LqSubCommand<CommandSender> {
    private final QuestCache questCache;

    public QuestIdsSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlats("quest", "ids");
        this.addArgument(QuestType.class, "type");
        this.questCache = plugin.getQuestCache();
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        QuestType questType = this.parseArgument(args, 2);

        if (questType.equals(QuestType.UNKNOWN)) {
            this.lang.local("invalid-quest-type", args[2]).to(commandSender);
            return;
        }
        this.lang.local("quest-ids-title").to(commandSender);
        for (Map.Entry<String, Quest> entry : this.questCache.getQuests(questType).entrySet()) {
            this.lang.local("quest-id", entry.getKey(), entry.getValue().getName()).to(commandSender);
        }
    }
}
