package io.github.luxuryquests.commands.lq;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.menu.UserDependent;
import io.github.luxuryquests.objects.quest.QuestType;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class LqCommand extends SimpleCommand<Player> {
    private final QuestsPlugin plugin;

    public LqCommand(QuestsPlugin plugin) {
        super(plugin, "lq", false);
        this.plugin = plugin;
        this.setSubCommands(
                new HelpSub(plugin),
                new InfoSub(plugin),
                new LicenseSub(plugin)
        );
        for (QuestType questType : TypeLoader.questTypes()) {
            this.getSubCommands().add(new MenuSub(plugin, questType.getName()));
        }
        for (String customMenu : this.plugin.getMenuLoader().getMenuNames()) {
            this.getSubCommands().add(new MenuSub(plugin, customMenu));
        }
        this.getSubCommands().add(new MenuSub(plugin, "portal"));
        this.getSubCommands().add(new MenuSub(plugin, "rewards"));
        this.getSubCommands().add(new MenuSub(plugin, "options"));

    }

    @Override
    public void onExecute(Player player, String[] strings) {
        Menu createdMenu = this.plugin.getMenuFactory().createMenu("portal", player);
        if (!(createdMenu instanceof UserDependent) || ((UserDependent) createdMenu).isUserViable()) {
            createdMenu.show();
        }
    }
}
