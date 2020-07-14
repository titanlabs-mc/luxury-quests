package io.github.luxuryquests.cache;

import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.exceptions.NoOnlineUserException;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.cache.FutureCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.save.Savable;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UserCache extends FutureCache<UUID, User> implements Savable {
    private final QuestsPlugin plugin;
    private final Storage<User> storage;
    private final QuestController questController;
    private Map<UserOptionType, String> defaultUserOptions = Maps.newHashMap();

    public UserCache(QuestsPlugin plugin, Storage<User> storage) {
        super(plugin);
        this.plugin = plugin;
        this.storage = storage;
        this.questController = plugin.getQuestController();
        this.setupUserOptions();
    }

    public User getOrThrow(UUID uuid) throws NoOnlineUserException {
        User user = this.getSync(uuid).orElse(null);
        if (user == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                throw new NoOnlineUserException("Could not find an online user with the uuid ".concat(FastUuid.toString(uuid)));
            } else {
                Text.sendMessage(player, "&cPlease re-log for this feature to work. We're sorry for the inconvenience.");
            }
            return null;
        }
        return user;
    }

    public CompletableFuture<User> load(UUID uuid) {
        return this.get(uuid).thenApply(maybeUser -> {
            if (!maybeUser.isPresent()) {
                User user = this.storage.load(FastUuid.toString(uuid));
                if (user == null) {
                    User wrappedUser = this.set(uuid, new User(uuid, this.defaultUserOptions));
                    wrappedUser.updateCompletedQuests(this.questController);
                    return wrappedUser;
                } else {
                    User wrappedUser = this.set(uuid, user);
                    wrappedUser.updateCompletedQuests(this.questController);
                    return wrappedUser;
                }
            }
            return maybeUser.get();
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void asyncModifyAll(Consumer<User> consumer) {
        this.plugin.runAsync(() -> {
            for (User user : this.values()) {
                consumer.accept(user);
            }
            for (User user : this.storage.loadAll()) {
                if (!this.hasKey(user.getUuid())) {
                    consumer.accept(user);
                    this.storage.save(user.getUuid().toString(), user);
                }
            }
        });
    }

    public void unload(UUID uuid, boolean invalidate) {
        this.get(uuid).thenAccept(maybeUser -> {
            maybeUser.ifPresent(user -> this.storage.save(FastUuid.toString(user.getUuid()), user));
            if (invalidate) {
                this.invalidate(uuid);
            }
        });
    }


    public void loadOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.load(player.getUniqueId());
        }
    }

    @Override
    public void save() {
        for (User user : this.getSubCache().asMap().values()) {
            this.storage.save(FastUuid.toString(user.getUuid()), user);
        }
    }

    private void setupUserOptions() {
        Config optionsConfig = this.plugin.getConfig("user-options");
        this.defaultUserOptions.put(UserOptionType.RESET_NOTIFICATIONS, String.valueOf(optionsConfig.bool("default-options.receive-reset-notifications")));
        this.defaultUserOptions.put(UserOptionType.AUTO_RECEIVE_REWARDS, String.valueOf(optionsConfig.bool("default-options.auto-receive-rewards")));
        this.defaultUserOptions.put(UserOptionType.PROGRESS_NOTIFICATIONS, optionsConfig.string("default-options.progression-notification-type"));
        this.defaultUserOptions.put(UserOptionType.COMPLETION_NOTIFICATIONS, optionsConfig.string("default-options.completion-notification-type"));
    }

    public Map<UserOptionType, String> getDefaultUserOptions() {
        return this.defaultUserOptions;
    }
}
