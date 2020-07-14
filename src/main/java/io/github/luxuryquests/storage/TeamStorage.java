package io.github.luxuryquests.storage;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.Team;
import io.github.luxuryquests.objects.user.TimedQuestData;
import io.github.luxuryquests.storage.adapters.TimedQuestDataAdapter;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;

public class TeamStorage extends Storage<Team> {

    public TeamStorage(QuestsPlugin questsPlugin) {
        super(questsPlugin, factory -> factory.create("json", path -> path.resolve("teams")));
        this.addAdapter(TimedQuestData.class, new TimedQuestDataAdapter());
        this.saveChanges();
    }

    @Override
    public Serializer<Team> serializer() {
        return null;
    }

    @Override
    public Deserializer<Team> deserializer() {
        return null;
    }
}
