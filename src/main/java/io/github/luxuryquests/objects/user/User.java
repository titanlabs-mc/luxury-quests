package io.github.luxuryquests.objects.user;

import com.google.common.collect.Lists;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.enums.UserOptionType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final UUID uuid;
    private QuestStore quests = new QuestStore();
    private List<String> pendingRewardIds = Lists.newArrayList();
    private Map<UserOptionType, String> options;
    private AtomicInteger persistentCompletedQuests = new AtomicInteger();
    private AtomicInteger liveCompletedQuests = new AtomicInteger();

    public User(UUID uuid, Map<UserOptionType, String> options) {
        this.uuid = uuid;
        this.options = options;
    }

    public User(UUID uuid, QuestStore quests, Map<String, String> options, List<String> pendingRewardIds, AtomicInteger persistentCompletedQuests) {
        this.uuid = uuid;
        this.quests = quests;
        this.options = UserOptionType.parseUserOptions(options);
        this.pendingRewardIds = pendingRewardIds;
        this.persistentCompletedQuests = persistentCompletedQuests;
    }


    public UUID getUuid() {
        return this.uuid;
    }

    public QuestStore getQuests() {
        return this.quests;
    }

    public List<String> getPendingRewardIds() {
        return this.pendingRewardIds;
    }

    public void setOptions(Map<UserOptionType, String> options) {
        this.options = options;
    }

    public Map<UserOptionType, String> getOptions() {
        return this.options;
    }

    public boolean getBooleanOption(String key) {
        return Boolean.parseBoolean(this.options.get(UserOptionType.parse(key)));
    }

    public Map<String, String> getSerializableOptions() {
        return UserOptionType.toSerializableUserOptions(this.options);
    }

    public String getOptionValue(UserOptionType type) {
        return this.options.get(type);
    }

    public boolean getBooleanOption(UserOptionType type) {
        return Boolean.parseBoolean(this.options.get(type));
    }

    public AtomicInteger getPersistentCompletedQuests() {
        return this.persistentCompletedQuests;
    }

    public AtomicInteger getAtomicLiveCompletedQuests() {
        return this.liveCompletedQuests;
    }

    public void updateCompletedQuests(QuestController questController) {
        this.liveCompletedQuests = new AtomicInteger(questController.calculateCompletedQuests(this));
    }
}
