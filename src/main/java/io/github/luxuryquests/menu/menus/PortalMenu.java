package io.github.luxuryquests.menu.menus;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.menu.Lockable;
import io.github.luxuryquests.menu.UserDependent;
import io.github.luxuryquests.menu.service.extensions.ConfigMenu;
import io.github.luxuryquests.objects.user.User;
import io.github.luxuryquests.quests.workers.QuestReset;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Replace;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PortalMenu extends ConfigMenu implements UserDependent, Lockable {
    private final User user;
    private final Collection<QuestReset> questResets;

    public PortalMenu(QuestsPlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
        this.questResets = plugin.getTypeLoader().getQuestResets().values();
        this.addUpdater(plugin, 20);
    }

    @Override
    public void redraw() {
        Replace replace = replacer -> {
            replacer.set("reward_option", this.user.getOptionValue(UserOptionType.AUTO_RECEIVE_REWARDS));
            replacer.set("progress_option", this.user.getOptionValue(UserOptionType.PROGRESS_NOTIFICATIONS));
            for (QuestReset questReset : this.questResets) {
                replacer.set(questReset.getQuestType().getName().concat("_time_left"), questReset.asString());
            }
            return replacer;
        };
        this.drawConfigItems(replace);
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
}
