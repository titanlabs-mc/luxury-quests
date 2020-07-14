package io.github.luxuryquests.cache;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.action.Action;
import io.github.luxuryquests.objects.reward.CommandReward;
import io.github.luxuryquests.objects.reward.ItemReward;
import io.github.luxuryquests.objects.reward.Reward;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.cache.SimpleCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.config.ConfigLoader;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class RewardCache extends SimpleCache<String, Reward<?>> {
    private final QuestsPlugin plugin;

    public RewardCache(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public Multiset<Reward<?>> getPendingRewards(User user) {
        Multiset<Reward<?>> rewards = HashMultiset.create();
        for (String id : user.getPendingRewardIds()) {
            this.get(id).ifPresent(rewards::add);
        }
        return rewards;
    }

    public void cache() {
        Config config = this.plugin.getConfig("rewards");
        ConfigLoader.reader(config)
                .readWrap(reader -> {
                    reader.keyLoop(rewardId -> {
                        String type = reader.string("type");
                        String name = reader.string("name") == null ? "Undefined" : reader.string("name");
                        List<String> loreAddon = reader.list("lore-addon");
                        Set<Action> actions = Sets.newLinkedHashSet();
                        for (String action : reader.list("actions")) {
                            actions.add(Action.parse(action));
                        }
                        if (type.equalsIgnoreCase("command")) {
                            this.set(rewardId, new CommandReward(rewardId, name, loreAddon, actions, HashMultiset.create(reader.list("commands"))));
                        } else if (type.equalsIgnoreCase("item")) {
                            Multiset<ItemStack> items = HashMultiset.create();
                            reader.keyLoop(rewardId.concat(".items"), itemKey -> items.add(reader.getItem("")));
                            this.set(rewardId, new ItemReward(rewardId, name, loreAddon, actions, items));
                        }
                    });
                });
    }
}
