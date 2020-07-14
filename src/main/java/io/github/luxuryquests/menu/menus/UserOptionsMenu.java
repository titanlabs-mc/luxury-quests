package io.github.luxuryquests.menu.menus;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.menu.Lockable;
import io.github.luxuryquests.menu.UserDependent;
import io.github.luxuryquests.menu.service.extensions.PageableConfigMenu;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class UserOptionsMenu extends PageableConfigMenu<Map.Entry<UserOptionType, String>> implements UserDependent, Lockable {
    private final User user;

    public UserOptionsMenu(QuestsPlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
    }

    @Override
    public MenuItem pageableItem(Map.Entry<UserOptionType, String> userOption) {
        UserOptionType type = userOption.getKey();
        String value = userOption.getValue();
        return MenuItem.builderOf(SpigotItem.toItem(this.config, "option-items", replacer -> replacer
                .set("option_name", type.getName())
                .set("user_option", value))
        ).onClick((menuItem, clickType) -> {
            this.user.getOptions().put(type, type.nextValue(value));
            this.refreshPageableItems();
        }).build();
    }

    @Override
    public ImmutablePair<Collection<Map.Entry<UserOptionType, String>>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(this.user.getOptions().entrySet(), MenuService.parseSlots(this, this.config, "option-slots"));
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
