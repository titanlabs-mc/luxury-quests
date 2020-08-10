package io.github.luxuryquests.quests.workers.pipeline.steps;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.SubQuest;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.objects.quest.variable.Variable;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.service.Locks;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

public class QuestValidationStep {
    private final CompletionStep completionStep;
    private final QuestController controller;
    private final ReentrantLock questLock = Locks.newReentrantLock();

    public QuestValidationStep(QuestsPlugin plugin) {
        this.completionStep = new CompletionStep(plugin);
        this.controller = plugin.getQuestController();
    }

    public void process(Player player, User user, String name, int progress, QuestResult questResult, Collection<Quest> quests, boolean overrideUpdate) {
        for (Quest quest : quests) {
            for (SubQuest subQuest : quest.getSubQuests()) {
                this.applyAntiAbuseMeasures(player, user, quest, subQuest, name, progress, questResult);
                if (!name.equalsIgnoreCase(subQuest.getType())) {
                    continue;
                }
                this.questLock.lock();
                try {
                    this.proceed(player, user, quest, subQuest, progress, questResult, overrideUpdate);
                } finally {
                    this.questLock.unlock();
                }
            }
        }
    }

    private void proceed(Player player, User user, Quest quest, SubQuest subQuest, int progress, QuestResult questResult, boolean overrideUpdate) {
        Variable subVariable = subQuest.getVariable();
        int subQuestProgress = this.controller.getSubQuestProgress(user, quest, subQuest);
        if (overrideUpdate && subQuestProgress == progress) {
            return;
        }
        if (subQuestProgress < subQuest.getRequiredProgress()
                && questResult.isEligible(player, subVariable)
                && this.controller.isTimedQuestValid(user, quest)
                && quest.getCompletionThreshold() <= user.getAtomicLiveCompletedQuests().intValue()
                && this.controller.areRequiredQuestsDone(user, quest)
                && this.controller.meetsCategoryRequirements(user, subQuest)
                && this.controller.hasRequiredPermissions(user, quest)) {
            this.completionStep.process(user, quest, subQuest, subQuestProgress, progress, overrideUpdate);
        }
    }

    private void applyAntiAbuseMeasures(Player player, User user, Quest quest, SubQuest subQuest, String currentType, int progress, QuestResult questResult) {
        if (this.controller.isQuestDone(user, quest)
                || (!currentType.equals("block-break") && !currentType.equals("block-place"))
                || !subQuest.isAntiAbuse()
                || questResult.isEligible(player, subQuest.getVariable())) {
            return;
        }
        if ((subQuest.getType().equals("block-break") && currentType.equals("block-place")) || (subQuest.getType().equals("block-place") && currentType.equals("block-break"))) {
            int currentProgress = this.controller.getSubQuestProgress(user, quest, subQuest);
            if (currentProgress > 0) {
                this.controller.setSubQuestProgress(user, quest, subQuest, Math.max(currentProgress - progress, 0));
            }
        }
    }
}