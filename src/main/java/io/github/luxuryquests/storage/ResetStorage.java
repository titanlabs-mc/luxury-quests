package io.github.luxuryquests.storage;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.ResetType;
import io.github.luxuryquests.quests.workers.QuestReset;
import me.hyfe.simplespigot.json.TypeTokens;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResetStorage extends Storage<QuestReset> {
    private final QuestsPlugin plugin;

    public ResetStorage(QuestsPlugin plugin) {
        super(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storageType"), path -> path.resolve("misc-storage")));
        this.plugin = plugin;
    }

    @Override
    public Serializer<QuestReset> serializer() {
        return (questReset, json, gson) -> {
            json.addProperty("type", questReset.getQuestType().getName());
            json.addProperty("time-unit", questReset.getTimeUnit().toString());
            json.addProperty("interval", gson.toJson(questReset.getUnitInterval()));
            json.addProperty("current-quests", gson.toJson(questReset.getCurrentQuests().stream().map(Quest::getId).collect(Collectors.toList())));
            json.addProperty("current-date", questReset.getWhenReset().toString());
            return json;
        };
    }

    @Override
    public Deserializer<QuestReset> deserializer() {
        return (json, gson) -> {
            ResetType resetType = TypeLoader.parseResetType(json.get("type").getAsString());
            String timeUnitAsString = json.get("time-unit") == null ? null : json.get("time-unit").getAsString();
            ChronoUnit timeUnit = ChronoUnit.valueOf(timeUnitAsString == null ? "DAYS" : timeUnitAsString.toUpperCase());
            long unitInterval = json.get("interval").getAsLong();
            List<String> currentQuests = gson.fromJson(json.get("current-quests").getAsString(), TypeTokens.findType());
            ZonedDateTime whenReset = ZonedDateTime.parse(json.get("current-date").getAsString());
            return new QuestReset(this.plugin, resetType, timeUnit, unitInterval, currentQuests
                    .stream()
                    .map(id -> this.plugin.getQuestCache().getQuest(resetType.getPartner(), id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()), questReset -> whenReset);
        };
    }
}
