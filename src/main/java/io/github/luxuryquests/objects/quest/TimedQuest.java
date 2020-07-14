package io.github.luxuryquests.objects.quest;

import com.google.common.collect.Multiset;
import io.github.luxuryquests.objects.reward.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class TimedQuest extends Quest {
    private final int timeToComplete; // seconds
    private final int completionLimit;
    private final int maxAttempts;

    public TimedQuest(String id,
                      String name,
                      ItemStack item,
                      Multiset<Reward<?>> rewards,
                      Set<SubQuest> subQuests,
                      QuestType questType,
                      int timeToComplete,
                      int completionLimit,
                      int maxAttempts,
                      int completionThreshold,
                      Set<String> requiredQuests,
                      Set<String> requiredPermissions,
                      Map<String, String> properties,
                      Set<Integer> notifyAt) {
        super(id, name, item, rewards, subQuests, questType, completionThreshold, requiredQuests, requiredPermissions, properties, notifyAt);
        this.timeToComplete = timeToComplete;
        this.completionLimit = completionLimit < 0 ? 1 : completionLimit;
        this.maxAttempts = maxAttempts < 0 ? 1 : maxAttempts;
    }

    public int getTimeToComplete() {
        return this.timeToComplete;
    }

    public int getCompletionLimit() {
        return this.completionLimit;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }
}
