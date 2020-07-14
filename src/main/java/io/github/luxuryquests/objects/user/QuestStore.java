package io.github.luxuryquests.objects.user;

import com.google.common.collect.Maps;
import io.github.luxuryquests.objects.quest.TimedQuest;

import java.util.Map;

public class QuestStore {
    private Map<String, Map<String, Map<String, Integer>>> quests = Maps.newConcurrentMap();
    private Map<String, Map<String, TimedQuestData>> timedQuestData = Maps.newConcurrentMap();

    public Map<String, Map<String, Map<String, Integer>>> asMap() {
        return this.quests;
    }

    public Map<String, Map<String, TimedQuestData>> asTimedMap() {
        return this.timedQuestData;
    }

    public TimedQuestData createTimedQuestData(TimedQuest quest) {
        TimedQuestData timedQuestData = new TimedQuestData();
        this.timedQuestData.put(quest.getType().getName(), Maps.newHashMap());
        this.timedQuestData.get(quest.getType().getName()).put(quest.getId(), timedQuestData);
        return timedQuestData;
    }
}
