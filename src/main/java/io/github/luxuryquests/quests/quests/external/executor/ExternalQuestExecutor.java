package io.github.luxuryquests.quests.quests.external.executor;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.variable.QuestResult;
import io.github.luxuryquests.quests.QuestExecutor;
import me.hyfe.simplespigot.text.Replace;
import org.bukkit.entity.Player;

import java.util.function.UnaryOperator;

public class ExternalQuestExecutor extends QuestExecutor {
    private String prefix;

    public ExternalQuestExecutor(QuestsPlugin plugin, String pluginName) {
        super(plugin);
        this.prefix = pluginName.concat("_");
    }


    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        super.execute(this.prefix.concat(name), player, progress, result, replace, overrideUpdate);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace) {
        super.execute(this.prefix.concat(name), player, progress, result, replace, false);
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
