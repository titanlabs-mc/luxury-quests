package io.github.luxuryquests.objects.reward;

import com.google.common.collect.Multiset;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.action.Action;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class CommandReward extends Reward<String> {

    public CommandReward(String id, String name, List<String> loreAddon, Set<Action> actions, Multiset<String> set) {
        super(id, name, loreAddon, actions, set);
    }

    @Override
    public void reward(QuestsPlugin plugin, Player player) {
        for (String command : this.set) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Replacer.to(command, replacer -> replacer.set("player", player.getName())));
        }
        this.runActions(plugin, player);
    }
}
