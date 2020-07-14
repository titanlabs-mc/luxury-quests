package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.SubQuest;
import io.github.luxuryquests.objects.user.User;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class ProgressQuestSub extends LqSubCommand<CommandSender> {
    private final QuestCache questCache;
    private final QuestController controller;

    public ProgressQuestSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlats("progress", "quest");
        this.addArgument(User.class, "player");
        this.addArgument(QuestType.class, "type");
        this.addArgument(String.class, "quest id");
        this.addArgument(String.class, "sub-quest id");
        this.addArgument(Integer.class, "amount");
        this.questCache = plugin.getQuestCache();
        this.controller = plugin.getQuestController();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        QuestType questType = this.parseArgument(args, 3);
        String id = this.parseArgument(args, 4);
        String subId = this.parseArgument(args, 5);
        int amount = this.parseArgument(args, 6);

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
        SubQuest subQuest = this.questCache.getSubQuest(questType, id, subId);
        if (subQuest == null) {
            this.lang.local("invalid-subquest-id", args[5], args[3].toLowerCase()).to(sender);
            return;
        }
        this.controller.addSubQuestProgress(user, quest, subQuest, amount);
        this.lang.local("successful-quest-progress", subQuest.getName(), quest.getName());
    }
}
