package io.github.luxuryquests.cache;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.checker.QuestChecker;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.SubQuest;
import io.github.luxuryquests.objects.quest.TimedQuest;
import io.github.luxuryquests.objects.quest.variable.Variable;
import io.github.luxuryquests.objects.reward.Reward;
import me.hyfe.simplespigot.cache.SimpleCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.config.ConfigLoader;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class QuestCache extends SimpleCache<String, Map<String, Quest>> {
    private final QuestChecker questChecker = new QuestChecker();
    private final QuestsPlugin plugin;
    private final Set<Integer> notifyAt;
    private final Set<String> placeholderTypes = Sets.newHashSet();

    public QuestCache(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.notifyAt = Sets.newHashSet(plugin.getConfig("settings").list("quests.notify-at-percentages"));
    }

    public Quest getQuest(QuestType questType, String id) {
        return this.getQuests(questType).get(id);
    }

    public Quest getQuest(String questType, String id) {
        return this.getQuests(questType).get(id);
    }

    public SubQuest getSubQuest(QuestType questType, String id, String subId) {
        return this.getSubQuest(questType.getName(), id, subId);
    }

    public SubQuest getSubQuest(String questType, String id, String subId) {
        Quest quest = this.getQuest(questType, id);
        if (quest != null) {
            for (SubQuest subQuest : quest.getSubQuests()) {
                if (subQuest.getId().equals(subId)) {
                    return subQuest;
                }
            }
        }
        return null;
    }

    public Map<String, Quest> getQuests(QuestType questType) {
        return this.getQuests(questType.getName());
    }

    public Map<String, Quest> getQuests(String questType) {
        return this.get(questType).orElseGet(() -> this.set(questType, Maps.newLinkedHashMap()));
    }

    public Set<String> getPlaceholderTypes() {
        return this.placeholderTypes;
    }

    public void cache() {
        RewardCache rewardCache = this.plugin.getRewardCache();
        for (QuestType type : TypeLoader.questTypes()) {
            Config config = type.getQuestsConfig();
            ConfigLoader.reader(config).readWrap(reader -> reader.keyLoop(questId -> {
                if (StringUtils.isNumeric(questId)) {
                    String questName = reader.string("name");
                    ItemStack questItem = reader.getItem("item");
                    Multiset<Reward<?>> rewards = HashMultiset.create();
                    for (String rewardId : reader.list("rewards")) {
                        rewardCache.get(rewardId).ifPresent(rewards::add);
                    }
                    Set<SubQuest> subQuests = Sets.newHashSet();
                    int timeToComplete = reader.integer("time-to-complete"); // seconds
                    int completionLimit = reader.integer("completion-limit");
                    int maxAttempts = reader.integer("max-attempts");
                    int completionThreshold = reader.integer("completion-threshold");
                    Set<String> requiredQuests = Sets.newHashSet(reader.list("required-quests"));
                    Set<String> requiredPermissions = Sets.newHashSet(reader.list("required-permissions"));
                    reader.keyLoop(questId, subId -> {
                        if (StringUtils.isNumeric(subId)) {
                            String subName = reader.string("name");
                            String subType = reader.string("type");
                            int requiredProgress = reader.integer("required-progress");
                            boolean antiAbuse = config.bool(reader.getCurrentPath().concat(".anti-abuse"));
                            Map<String, Integer> categoryRequirements = Maps.newHashMap();
                            for (String entry : reader.list("category-requirements")) {
                                String[] splitEntry = entry.split(":");
                                categoryRequirements.put(splitEntry[0], Integer.parseInt(splitEntry[1]));
                            }
                            // Sub quest validation checking
                            if (!this.questChecker.validateSubQuest(type.getName(), questId, subId, subName, subType, requiredProgress)) {
                                return;
                            }
                            if (subType.startsWith("placeholderapi_")) {
                                this.placeholderTypes.add(subType);
                            }
                            subQuests.add(new SubQuest(subId, subName, subType, Variable.of(reader), requiredProgress, antiAbuse, categoryRequirements));
                        }
                    });
                    // Quest validation checking
                    if (!this.questChecker.validateQuest(type.getName(), questId, questItem, questName, subQuests)) {
                        return;
                    }
                    this.getQuests(type).put(questId, timeToComplete < 0 ?
                            new Quest(
                                    questId,
                                    questName,
                                    questItem,
                                    rewards,
                                    subQuests,
                                    type,
                                    completionThreshold,
                                    requiredQuests,
                                    requiredPermissions,
                                    Maps.newHashMap(),
                                    this.notifyAt) :
                            new TimedQuest(
                                    questId,
                                    questName,
                                    questItem,
                                    rewards,
                                    subQuests,
                                    type,
                                    timeToComplete,
                                    completionLimit,
                                    maxAttempts,
                                    completionThreshold,
                                    requiredQuests,
                                    requiredPermissions,
                                    Maps.newHashMap(),
                                    this.notifyAt));
                }
            }));
        }
    }
}
