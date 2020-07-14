package io.github.luxuryquests.menu;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.menu.menus.PortalMenu;
import io.github.luxuryquests.menu.menus.RewardMenu;
import io.github.luxuryquests.menu.menus.UserOptionsMenu;
import io.github.luxuryquests.menu.menus.questsmenus.ResetQuestMenu;
import io.github.luxuryquests.menu.menus.questsmenus.StandardQuestMenu;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.ResetType;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class MenuFactory {
    private final QuestsPlugin plugin;

    public MenuFactory(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public Menu createMenu(String menuName, Player player) {
        switch (menuName) {
            case "portal":
                return new PortalMenu(this.plugin, this.plugin.getConfig("portal-menu"), player);
            case "options":
                return new UserOptionsMenu(this.plugin, this.plugin.getConfig("user-menu"), player);
            case "rewards":
                return new RewardMenu(this.plugin, this.plugin.getConfig("rewards-menu"), player);
            default: {
                ResetType resetType = TypeLoader.parseResetType(menuName.replace("-quests", ""));
                if (!resetType.equals(ResetType.UNKNOWN)) {
                    return new ResetQuestMenu(this.plugin, resetType.getPartner().getMenuConfig(), player, resetType);
                }
                QuestType questType = TypeLoader.parseQuestType(menuName.replace("-quests", ""));
                if (!questType.equals(QuestType.UNKNOWN)) {
                    return new StandardQuestMenu(this.plugin, questType.getMenuConfig(), player, questType);
                }
                return this.plugin.getMenuLoader().createMenu(player, menuName);
            }
        }
    }
}
