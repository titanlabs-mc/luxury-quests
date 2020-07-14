package io.github.luxuryquests.menu.menus;

import com.google.common.collect.Lists;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.RewardCache;
import io.github.luxuryquests.menu.Lockable;
import io.github.luxuryquests.menu.UserDependent;
import io.github.luxuryquests.menu.service.extensions.PageableConfigMenu;
import io.github.luxuryquests.objects.reward.Reward;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RewardMenu extends PageableConfigMenu<Reward<?>> implements UserDependent, Lockable {
    private final User user;
    private final RewardCache rewardCache;

    public RewardMenu(QuestsPlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
        this.rewardCache = plugin.getRewardCache();

    }

    @Override
    public MenuItem pageableItem(Reward<?> reward) {
        return MenuItem.builderOf(this.createRewardsItem(reward))
                .onClick((menuItem, clickType) -> {
                    this.user.getPendingRewardIds().remove(reward.getId());
                    reward.reward(this.plugin, this.player);
                    this.refreshPageableItems();
                })
                .build();
    }

    @Override
    public ImmutablePair<Collection<Reward<?>>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(this.rewardCache.getPendingRewards(this.user), MenuService.parseSlots(this, this.config, "reward-slots"));
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }

    @Override
    public boolean isLocked() {
        return this.config.bool("locked");
    }

    @Override
    public void show() {
        if (this.isLocked()) {
            return;
        }
        super.show();
    }

    private ItemStack createRewardsItem(Reward<?> reward) {
        ItemStack itemStack = SpigotItem.toItem(this.config, "reward-items", replacer -> replacer.set("reward_name", reward.getName()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (Objects.nonNull(itemMeta)) {
            List<String> currentLore = itemMeta.getLore() == null ? Lists.newArrayList() : itemMeta.getLore();
            List<String> newLore = Lists.newArrayList();
            for (String line : currentLore) {
                if (line.contains("%reward_lore_addon%")) {
                    newLore.addAll(Text.modify(reward.getLoreAddon()));
                } else {
                    newLore.add(line);
                }
            }
            itemMeta.setLore(newLore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
