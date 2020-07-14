package io.github.luxuryquests.quests;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.ExecutableQuestResult;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.workers.pipeline.QuestPipeline;
import me.hyfe.simplespigot.text.Replace;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.function.UnaryOperator;

public class QuestExecutor implements Listener {
    private final QuestPipeline questPipeline;

    public QuestExecutor(QuestsPlugin plugin) {
        this.questPipeline = plugin.getQuestPipeline();
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), overrideUpdate);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), false);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, 1, result, replace, false);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer, false);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result) {
        this.execute(name, player, 1, result);
    }
}
