package io.github.luxuryquests.loaders;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.ResetType;
import io.github.luxuryquests.quests.workers.QuestReset;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.storage.storage.Storage;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeLoader {
    private final QuestsPlugin plugin;
    private final Path dataFolder;
    private final Storage<QuestReset> resetStorage;
    private final Map<String, QuestReset> questResets = Maps.newHashMap();

    private final static Set<QuestType> questTypes = Sets.newHashSet();
    private final static Set<ResetType> resetTypes = Sets.newHashSet();

    public TypeLoader(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath();
        this.resetStorage = plugin.getResetStorage();
    }

    public static Set<QuestType> questTypes() {
        return questTypes;
    }

    public static Set<ResetType> resetTypes() {
        return resetTypes;
    }

    public static QuestType parseQuestType(String type) {
        for (QuestType questType : questTypes()) {
            if (questType.getName().equalsIgnoreCase(type)) {
                return questType;
            }
        }
        return QuestType.UNKNOWN;
    }

    public static ResetType parseResetType(String type) {
        for (ResetType resetType : resetTypes()) {
            if (resetType.getPartner().getName().equalsIgnoreCase(type)) {
                return resetType;
            }
        }
        return ResetType.UNKNOWN;
    }

    public Map<String, QuestReset> getQuestResets() {
        return this.questResets;
    }

    public Set<Collection<Quest>> gatherAll(QuestCache questCache) {
        Set<Collection<Quest>> quests = Sets.newHashSet();
        for (QuestType questType : questTypes()) {
            if (questType.isStandard()) {
                quests.add(questCache.getQuests(questType).values());
            }
        }
        for (ResetType resetType : resetTypes()) {
            quests.add(resetType.getQuestReset().getCurrentQuests());
        }
        return quests;
    }

    @SneakyThrows
    public void load() {
        this.createDefaultFiles();
        Set<File> questTypes = Files.walk(this.dataFolder.resolve("quests"))
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith("-quests.yml"))
                .collect(Collectors.toSet());
        for (File questsFile : questTypes) {
            String name = questsFile.getName().replace("-quests.yml", "");
            File menuFile = this.dataFolder.resolve("menus").resolve(questsFile.getName()).toFile();
            if (!menuFile.exists()) {
                Bukkit.getLogger().warning("Failed to load the quest type ".concat(name).concat(". Could not find the file menus/".concat(questsFile.getName())));
                continue;
            }
            Config questsConfig = new Config(this.plugin, questsFile, true);
            Config menuConfig = new Config(this.plugin, menuFile, true);
            QuestType questType = new QuestType(name, questsConfig, menuConfig);
            questTypes().add(questType);
            if (questsConfig.has("settings.reset-interval")) {
                resetTypes().add(new ResetType(questsFile.getName().replace(".yml", ""), questsConfig, questType, this.questResets::get));
                questType.toggleStandard();
            }
            Bukkit.getLogger().info("Successfully loaded the quest type ".concat(name));
        }
    }

    public void loadQuestResets() {
        for (ResetType resetType : resetTypes()) {
            QuestReset questReset = this.resetStorage.load(resetType.getId());
            QuestReset result = questReset == null ? new QuestReset(this.plugin, resetType, resetType.getTimeUnit(), resetType.getUnitInterval(), Sets.newHashSet()) : questReset;
            this.questResets.put(resetType.getId(), result);
            result.start();
        }
    }

    public void saveQuestResets() {
        for (Map.Entry<String, QuestReset> entry : this.questResets.entrySet()) {
            this.resetStorage.save(entry.getKey(), entry.getValue());
        }
    }

    private void createDefaultFiles() {
        Path path = this.dataFolder.resolve("quests");
        if (path.toFile().exists()) {
            return;
        }
        this.plugin.saveResource("quests/daily-quests.yml", false);
        this.plugin.saveResource("quests/standard-quests.yml", false);
        this.plugin.saveResource("quests/weekly-quests.yml", false);
        this.plugin.saveResource("menus/daily-quests.yml", false);
        this.plugin.saveResource("menus/standard-quests.yml", false);
        this.plugin.saveResource("menus/weekly-quests.yml", false);
    }
}
