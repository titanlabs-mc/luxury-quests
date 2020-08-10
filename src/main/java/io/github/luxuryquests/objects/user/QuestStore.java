package io.github.luxuryquests.objects.user;

import io.github.luxuryquests.objects.quest.TimedQuest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestStore {
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> quests = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ConcurrentHashMap<String, TimedQuestData>> timedQuestData = new ConcurrentHashMap<>();

    public Map<String, ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>> asMap() {
        return this.quests;
    }

    public Map<String, ConcurrentHashMap<String, TimedQuestData>> asTimedMap() {
        return this.timedQuestData;
    }

    public TimedQuestData createTimedQuestData(TimedQuest quest) {
        String questType = quest.getType().getName();
        TimedQuestData timedQuestData = new TimedQuestData();
        if (!this.timedQuestData.containsKey(questType)) {
            this.timedQuestData.put(questType, new ConcurrentHashMap<>());
        }
        this.timedQuestData.get(questType).put(quest.getId(), timedQuestData);
        return timedQuestData;
    }
}
