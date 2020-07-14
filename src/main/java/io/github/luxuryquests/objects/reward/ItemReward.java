package io.github.luxuryquests.objects.reward;

import com.google.common.collect.Multiset;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.action.Action;
import me.hyfe.simplespigot.service.simple.Simple;
import me.hyfe.simplespigot.service.simple.services.SpigotService;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ItemReward extends Reward<ItemStack> {

    public ItemReward(String id, String name, List<String> loreAddon, Set<Action> actions, Multiset<ItemStack> set) {
        super(id, name, loreAddon, actions, set);
    }

    @Override
    public void reward(QuestsPlugin plugin, Player player) {
        Simple.spigot().giveItem(player, this.set);
        this.runActions(plugin, player);
    }
}
