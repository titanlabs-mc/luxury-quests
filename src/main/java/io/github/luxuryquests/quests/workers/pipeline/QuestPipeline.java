package io.github.luxuryquests.quests.workers.pipeline;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.workers.pipeline.steps.QuestValidationStep;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class QuestPipeline {
    private final QuestValidationStep questValidationStep;
    private final UserCache userCache;
    private final TypeLoader typeLoader;
    private final QuestCache questCache;

    public QuestPipeline(QuestsPlugin plugin) {
        this.questValidationStep = new QuestValidationStep(plugin);
        this.userCache = plugin.getUserCache();
        this.typeLoader = plugin.getTypeLoader();
        this.questCache = plugin.getQuestCache();
    }

    @SneakyThrows
    public void handle(String name, Player player, int progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        if (player == null) {
            return;
        }
        this.userCache.get(player.getUniqueId()).thenAccept(maybeUser -> maybeUser.ifPresent(user -> {
            for (Collection<Quest> questCollection : this.typeLoader.gatherAll(this.questCache)) {
                this.questValidationStep.process(player, user, name, progress, questResult, questCollection, overrideUpdate);
            }
        })).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
