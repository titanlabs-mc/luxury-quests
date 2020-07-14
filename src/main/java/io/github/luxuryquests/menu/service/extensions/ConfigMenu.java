package io.github.luxuryquests.menu.service.extensions;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.MenuIllustrator;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.Menu;
import me.hyfe.simplespigot.text.Replace;
import org.bukkit.entity.Player;

import java.util.Map;


public abstract class ConfigMenu extends Menu {
    protected final QuestsPlugin plugin;
    protected final Config config;
    protected final MenuIllustrator illustrator;
    private Map<String, Runnable> dynamicActions;

    public ConfigMenu(QuestsPlugin plugin, Config config, Player player) {
        super(player, config.string("menu-title"), config.integer("menu-rows"));
        this.plugin = plugin;
        this.config = config;
        this.illustrator = plugin.getMenuIllustrator();
    }

    @Override
    public void redraw() {
        this.drawConfigItems(null);
    }

    public void drawConfigItems(Replace replace) {
        this.illustrator.draw(this, this.config, this.plugin.getMenuFactory(), this.player, this.plugin.getActionCache(), this.dynamicActions, replace);
    }

    public void dynamicAction(String value, Runnable runnable) {
        this.dynamicActions.put(value, runnable);
    }
}
