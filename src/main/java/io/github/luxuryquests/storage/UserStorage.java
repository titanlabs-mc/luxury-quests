package io.github.luxuryquests.storage;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.user.QuestStore;
import io.github.luxuryquests.objects.user.TimedQuestData;
import io.github.luxuryquests.objects.user.User;
import io.github.luxuryquests.storage.adapters.TimedQuestDataAdapter;
import io.github.luxuryquests.updaters.UserUpdater;
import me.hyfe.simplespigot.json.TypeTokens;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;
import me.hyfe.simplespigot.uuid.FastUuid;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class UserStorage extends Storage<User> {
    private final UserUpdater updater;

    public UserStorage(QuestsPlugin plugin) {
        super(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storageType"), path -> path, "users"));
        this.updater = plugin.getUserUpdater();
        this.addAdapter(TimedQuestData.class, new TimedQuestDataAdapter());
        this.saveChanges();
    }

    @Override
    public Serializer<User> serializer() {
        return (user, json, gson) -> {
            json.addProperty("uuid", user.getUuid().toString());
            json.addProperty("options", gson.toJson(user.getSerializableOptions()));
            json.addProperty("quests", gson.toJson(user.getQuests()));
            json.addProperty("pending-rewards", gson.toJson(user.getPendingRewardIds()));
            json.addProperty("persistentCompletedQuests", user.getPersistentCompletedQuests().intValue());
            return json;
        };
    }

    @Override
    public Deserializer<User> deserializer() {
        return (json, gson) -> {
            UUID uuid = FastUuid.parse(json.get("uuid").getAsString());
            Map<String, String> options = gson.fromJson(json.get("options").getAsString(), TypeTokens.findType());
            QuestStore quests = gson.fromJson(json.get("quests").getAsString(), QuestStore.class);
            List<String> pendingRewardIds = gson.fromJson(json.get("pending-rewards").getAsString(), TypeTokens.findType());
            AtomicInteger persistentCompletedQuests = json.get("persistentCompletedQuests") == null ? new AtomicInteger() : new AtomicInteger(json.get("persistentCompletedQuests").getAsInt());
            return this.updater.update(new User(uuid, quests, options, pendingRewardIds, persistentCompletedQuests));
        };
    }
}
