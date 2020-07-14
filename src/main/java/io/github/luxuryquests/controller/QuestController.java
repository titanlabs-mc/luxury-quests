package io.github.luxuryquests.controller;

import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.SubQuest;
import io.github.luxuryquests.objects.quest.TimedQuest;
import io.github.luxuryquests.objects.user.TimedQuestData;
import io.github.luxuryquests.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class QuestController {
    private final QuestCache questCache;

    public QuestController(QuestsPlugin plugin) {
        this.questCache = plugin.getQuestCache();
    }

    /*
     * common
     */
    private final Function<User, Map<String, Map<String, Map<String, Integer>>>> questMap = user -> user.getQuests().asMap();

    /*
     * quests
     */
    public Map<String, Map<String, Integer>> getQuests(User user, QuestType questType) {
        return this.questMap.apply(user).get(questType.getName());
    }

    public int getQuestProgress(User user, Quest quest) {
        int sum = 0;
        for (SubQuest subQuest : quest.getSubQuests()) {
            sum += this.getSubQuestProgress(user, quest, subQuest);
        }
        return sum;
    }

    public int getQuestProgressPercentage(User user, Quest quest) {
        int progressSum = 0;
        int totalSum = 0;
        for (SubQuest subQuest : quest.getSubQuests()) {
            progressSum += this.getSubQuestProgress(user, quest, subQuest);
            totalSum += subQuest.getRequiredProgress();
        }
        return (progressSum / totalSum) * 100;
    }

    public boolean isQuestDone(User user, Quest quest) {
        for (SubQuest subQuest : quest.getSubQuests()) {
            if (this.getSubQuestProgress(user, quest, subQuest) < subQuest.getRequiredProgress()) {
                return false;
            }
        }
        return true;
    }

    public boolean areRequiredQuestsDone(User user, Quest quest) {
        for (String id : quest.getRequiredQuests()) {
            Quest wrappedQuest = this.questCache.getQuest(quest.getType(), id);
            if (!this.isQuestDone(user, wrappedQuest)) {
                return false;
            }
        }
        return true;
    }

    public boolean meetsCategoryRequirements(User user, SubQuest subQuest) {
        for (Map.Entry<String, Integer> entry : subQuest.getCategoryRequirements().entrySet()) {
            if (this.calculateCompletedQuests(user, entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasRequiredPermissions(User user, Quest quest) {
        Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) {
            return true;
        }
        for (String permission : quest.getRequiredPermissions()) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean resetQuest(User user, Quest quest, boolean timedQuest) {
        Map<String, Map<String, Integer>> quests = this.getQuests(user, quest.getType());
        if (quests == null) {
            return false;
        }
        quests.remove(quest.getId());
        if (timedQuest) {
            Map<String, Map<String, TimedQuestData>> map = this.timedQuestMap(user);
            if (map == null || !map.containsKey(quest.getType().getName())) {
                return false;
            }
            map.get(quest.getType().getName()).remove(quest.getId());
        }
        return true;
    }

    public int calculateCompletedQuests(User user) {
        int completed = 0;
        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : user.getQuests().asMap().entrySet()) {
            Optional<Map<String, Quest>> maybeQuests = this.questCache.get(entry.getKey());
            if (maybeQuests.isPresent()) {
                for (String id : maybeQuests.get().keySet()) {
                    Quest quest = maybeQuests.get().get(id);
                    if (quest == null) {
                        continue;
                    }
                    if (this.isQuestDone(user, quest)) {
                        completed++;
                    }
                }
            }
        }
        return completed;
    }

    public int getPercentageCategoryCompletion(User user, String category) {
        int quests = 0;
        int totalPercentage = 0;
        Map<String, Map<String, Map<String, Integer>>> questStoreMap = user.getQuests().asMap();
        if (!questStoreMap.containsKey(category)) {
            return 0;
        }
        for (Quest quest : this.questCache.getQuests(category).values()) {
            quests++;
            totalPercentage += this.getQuestProgressPercentage(user, quest);
        }
        return totalPercentage / quests;
    }

    public int calculateCompletedQuests(User user, String category) {
        int completed = 0;
        Map<String, Map<String, Map<String, Integer>>> questStoreMap = user.getQuests().asMap();
        if (!questStoreMap.containsKey(category)) {
            return 0;
        }
        for (Map.Entry<String, Quest> entry : this.questCache.getQuests(category).entrySet()) {
            if (this.isQuestDone(user, entry.getValue())) {
                completed++;
            }
        }
        return completed;
    }

    /*
     * timed-quests
     */
    public Map<String, Map<String, TimedQuestData>> timedQuestMap(User user) {
        return user.getQuests().asTimedMap();
    }

    public boolean isTimedQuestDataPresent(User user, Quest quest) {
        return this.timedQuestMap(user).containsKey(quest.getType().getName()) && this.timedQuestMap(user).get(quest.getType().getName()).containsKey(quest.getId());
    }

    public boolean isTimedQuestValid(User user, Quest quest) {
        Boolean bool = this.applyTimedQuestData(user, quest, (data, timedQuest) -> data.isActive()
                && !data.exceededTimeLimit(timedQuest)
                && data.getCompletions() <= timedQuest.getCompletionLimit()
                && data.getAttempts() <= timedQuest.getMaxAttempts());
        return bool == null ? !(quest instanceof TimedQuest) : bool;
    }

    public TimedQuestData getTimedQuestData(User user, Quest quest) {
        return this.isTimedQuestDataPresent(user, quest) ? this.timedQuestMap(user).get(quest.getType().getName()).get(quest.getId()) : null;
    }

    public void failTimedMission(User user, Quest quest, TimedQuestData timedQuestData) {
        this.resetQuest(user, quest, false);
        timedQuestData.stop();
    }

    public <T> T applyTimedQuest(Quest quest, Function<TimedQuest, T> function) {
        return this.applyTimedQuest(quest, function, null);
    }

    public <T> T applyTimedQuestData(User user, Quest quest, BiFunction<TimedQuestData, TimedQuest, T> function) {
        return this.applyTimedQuestData(user, quest, function, null);
    }

    public <T> T applyTimedQuest(Quest quest, Function<TimedQuest, T> function, T def) {
        if (quest instanceof TimedQuest) {
            return function.apply((TimedQuest) quest);
        }
        return def;
    }

    public <T> T applyTimedQuestData(User user, Quest quest, BiFunction<TimedQuestData, TimedQuest, T> function, T def) {
        if (quest instanceof TimedQuest && this.isTimedQuestDataPresent(user, quest)) {
            return function.apply(this.getTimedQuestData(user, quest), (TimedQuest) quest);
        }
        return def;
    }

    /*
     * sub-quests
     */
    public Map<String, Integer> getSubQuests(User user, Quest quest) {
        return this.getQuests(user, quest.getType()).get(quest.getId());
    }

    public int getSubQuestProgress(User user, Quest quest, SubQuest subQuest) {
        return this.failedIndex(user, quest, subQuest.getId()).equals(FailedIndex.NONE) ? this.getSubQuests(user, quest).get(subQuest.getId()) : 0;
    }

    public int setSubQuestProgress(User user, Quest quest, SubQuest subQuest, int progress) {
        this.fillFailedIndexes(user, quest, subQuest.getId());
        this.getSubQuests(user, quest).put(subQuest.getId(), Math.min(subQuest.getRequiredProgress(), progress));
        return this.getSubQuestProgress(user, quest, subQuest);
    }

    public int addSubQuestProgress(User user, Quest quest, SubQuest subQuest, int toAdd) {
        return this.setSubQuestProgress(user, quest, subQuest, this.getSubQuestProgress(user, quest, subQuest) + toAdd);
    }

    private void fillFailedIndexes(User user, Quest quest, String subId) {
        Predicate<FailedIndex> failedIndex = index -> this.failedIndex(user, quest, subId).equals(index);
        if (failedIndex.test(FailedIndex.TYPE_LAYER)) {
            this.questMap.apply(user).put(quest.getType().getName(), Maps.newHashMap());
        }
        if (failedIndex.test(FailedIndex.QUEST_LAYER)) {
            this.getQuests(user, quest.getType()).put(quest.getId(), Maps.newHashMap());
        }
        if (failedIndex.test(FailedIndex.SUBQUEST_LAYER)) {
            this.getQuests(user, quest.getType()).get(quest.getId()).put(subId, 0);
        }
    }

    private FailedIndex failedIndex(User user, Quest quest, String subId) {
        if (!this.questMap.apply(user).containsKey(quest.getType().getName())) {
            return FailedIndex.TYPE_LAYER;
        }
        if (!this.getQuests(user, quest.getType()).containsKey(quest.getId())) {
            return FailedIndex.QUEST_LAYER;
        }
        if (!this.getQuests(user, quest.getType()).get(quest.getId()).containsKey(subId)) {
            return FailedIndex.SUBQUEST_LAYER;
        }
        return FailedIndex.NONE;
    }

    private enum FailedIndex {
        TYPE_LAYER, QUEST_LAYER, SUBQUEST_LAYER, NONE;
    }
}
