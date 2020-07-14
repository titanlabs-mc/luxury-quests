package io.github.luxuryquests.menu.menus.functional;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.extensions.ConfigMenu;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.entity.Player;

public class ConfirmMenu extends ConfigMenu {

    public static void of(QuestsPlugin plugin, Player player, Runnable runnable) {
        new ConfirmMenu(plugin, plugin.getConfig("confirm"), player, runnable);
    }

    public ConfirmMenu(QuestsPlugin plugin, Config config, Player player, Runnable runnable) {
        super(plugin, config, player);
        this.dynamicAction("confirm", runnable);
        this.dynamicAction("deny", this::close);
    }
}
