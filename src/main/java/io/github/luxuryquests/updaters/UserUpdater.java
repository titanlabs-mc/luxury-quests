package io.github.luxuryquests.updaters;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.config.Config;

import java.util.Map;

public class UserUpdater {
    private final Config options;

    public UserUpdater(QuestsPlugin plugin) {
        this.options = plugin.getConfig("user-options");
    }

    public User update(User user) {
        user.setOptions(this.updateOptions(user.getOptions()));
        return user;
    }

    private Map<UserOptionType, String> updateOptions(Map<UserOptionType, String> userOptions) {
        if (!userOptions.containsKey(UserOptionType.COMPLETION_NOTIFICATIONS)) {
            userOptions.put(UserOptionType.COMPLETION_NOTIFICATIONS, this.options.string("default-options.completion-notification-type").toLowerCase());
        }
        if (!userOptions.containsKey(UserOptionType.RESET_NOTIFICATIONS)) {
            userOptions.put(UserOptionType.RESET_NOTIFICATIONS, this.options.string("default-options.receive-reset-notifications").toLowerCase());
        }
        if (!userOptions.containsKey(UserOptionType.PROGRESS_NOTIFICATIONS)) {
            userOptions.put(UserOptionType.PROGRESS_NOTIFICATIONS, this.options.string("default-options.progress-notification-type").toLowerCase());
        }
        return userOptions;
    }
}
