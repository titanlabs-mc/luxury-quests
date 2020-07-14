package io.github.luxuryquests.api;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.RewardCache;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.reward.Reward;
import io.github.luxuryquests.objects.user.User;
import io.github.luxuryquests.registry.QuestRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Api {
    private final QuestsPlugin plugin;
    private final UserCache userCache;
    private final RewardCache rewardCache;
    private final QuestRegistry questRegistry;

    public Api(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getUserCache();
        this.rewardCache = plugin.getRewardCache();
        this.questRegistry = plugin.getQuestRegistry();
    }

    public QuestRegistry getQuestRegistry() {
        return this.questRegistry;
    }

    public CompletableFuture<Optional<User>> getUser(UUID uuid) {
        return this.userCache.get(uuid);
    }

    public void reward(UUID uuid, Quest quest) {
        if (uuid == null) {
            return;
        }
        for (Reward<?> reward : quest.getRewards()) {
            this.reward(uuid, reward.getId(), false);
        }
    }

    public void reward(UUID uuid, String rewardId, boolean bypass) {
        Player player = Bukkit.getPlayer(uuid);
        Optional<User> maybeUser = this.userCache.getSync(uuid);
        Optional<Reward<?>> maybeReward = this.rewardCache.get(rewardId);
        if (maybeReward.isPresent()) {
            Reward<?> reward = maybeReward.get();
            if (maybeUser.isPresent() && Objects.nonNull(player)) {
                User user = maybeUser.get();
                if (!user.getBooleanOption(UserOptionType.AUTO_RECEIVE_REWARDS) && !bypass) {
                    user.getPendingRewardIds().add(reward.getId());
                } else {
                    reward.reward(this.plugin, player);
                }
            }
        }
    }
}
