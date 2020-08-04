package io.github.luxuryquests.objects.user;

import com.google.common.collect.Maps;
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
        TimedQuestData timedQuestData = new TimedQuestData();
        this.timedQuestData.put(quest.getType().getName(), new ConcurrentHashMap<>());
        this.timedQuestData.get(quest.getType().getName()).put(quest.getId(), timedQuestData);
        return timedQuestData;
    }
}
