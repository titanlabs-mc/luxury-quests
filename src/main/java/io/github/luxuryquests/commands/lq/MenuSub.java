package io.github.luxuryquests.commands.lq;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import org.bukkit.entity.Player;

public class MenuSub extends LqSubCommand<Player> {
    private final String menuName;

    public MenuSub(QuestsPlugin plugin, String menuName) {
        super(plugin, false);
        this.menuName = menuName;

        this.addFlatWithAliases("open", "menu");
        this.addFlat(menuName);
    }

    @Override
    public void onExecute(Player player, String[] strings) {
        this.plugin.getMenuFactory().createMenu(this.menuName, player).show();
    }
}
