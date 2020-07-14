package io.github.luxuryquests.quests.workers.pipeline.steps;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.user.User;

public class RewardStep {
    private final QuestsPlugin plugin;

    public RewardStep(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void process(User user, Quest quest) {
        this.plugin.runSync(() -> this.plugin.getLocalApi().reward(user.getUuid(), quest));
    }
}
