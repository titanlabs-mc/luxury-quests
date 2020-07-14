package io.github.luxuryquests.objects.quest;

import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.github.luxuryquests.objects.reward.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class Quest {
    private final String id;
    private final String name;
    private final ItemStack item;
    private final Multiset<Reward<?>> rewards;
    private final Set<SubQuest> subQuests;
    private final QuestType questType;
    private final int completionThreshold;
    private final Set<String> requiredQuests;
    private final Set<String> requiredPermissions;
    private final int requiredProgress;
    private final Map<String, String> properties;
    private final Set<Integer> notifyAt = Sets.newHashSet();

    public Quest(String id,
                 String name,
                 ItemStack item,
                 Multiset<Reward<?>> rewards,
                 Set<SubQuest> subQuests,
                 QuestType questType,
                 int completionThreshold,
                 Set<String> requiredQuests,
                 Set<String> requiredPermissions,
                 Map<String, String> properties,
                 Set<Integer> notifyAt) {
        this.id = id;
        this.name = name;
        this.item = item;
        this.rewards = rewards;
        this.subQuests = subQuests;
        this.questType = questType;
        this.completionThreshold = Math.max(completionThreshold, 0);
        this.requiredQuests = requiredQuests;
        this.requiredPermissions = requiredPermissions;
        this.properties = properties;
        this.requiredProgress = subQuests.stream().mapToInt(SubQuest::getRequiredProgress).sum();
        for (int percentage : notifyAt) {
            this.notifyAt.add(this.requiredProgress * percentage / 100);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Multiset<Reward<?>> getRewards() {
        return this.rewards;
    }

    public Set<SubQuest> getSubQuests() {
        return this.subQuests;
    }

    public QuestType getType() {
        return this.questType;
    }

    public int getCompletionThreshold() {
        return this.completionThreshold;
    }

    public Set<String> getRequiredQuests() {
        return this.requiredQuests;
    }

    public Set<String> getRequiredPermissions() {
        return this.requiredPermissions;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public int getRequiredProgress() {
        return this.requiredProgress;
    }

    public Set<Integer> getNotifyAt() {
        return this.notifyAt;
    }
}
