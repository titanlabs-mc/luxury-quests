package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.TimedQuest;
import io.github.luxuryquests.objects.user.User;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class ResetQuestSub extends LqSubCommand<CommandSender> {
    private final QuestCache questCache;
    private final QuestController controller;

    public ResetQuestSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlats("reset", "quest");
        this.addArgument(User.class, "player");
        this.addArgument(QuestType.class, "type");
        this.addArgument(String.class, "id");
        this.questCache = plugin.getQuestCache();
        this.controller = plugin.getQuestController();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        QuestType questType = this.parseArgument(args, 3);
        String id = this.parseArgument(args, 4);

        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        User user = maybeUser.get();
        if (questType.equals(QuestType.UNKNOWN)) {
            this.lang.local("invalid-quest-type", args[3]).to(sender);
            return;
        }
        Quest quest = this.questCache.getQuest(questType, id);
        if (quest == null) {
            this.lang.local("invalid-quest-id", args[4], args[3].toLowerCase()).to(sender);
            return;
        }
        boolean success = this.controller.resetQuest(user, quest, quest instanceof TimedQuest);
        this.lang.local(success ? "successful-quest-reset" : "failed-quest-reset", quest.getName(), args[2]).to(sender);
    }
}
