package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.lqa.materialsub.MaterialBlockSub;
import io.github.luxuryquests.commands.lqa.materialsub.MaterialItemSub;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class LqaCommand extends SimpleCommand<CommandSender> {
    public LqaCommand(QuestsPlugin plugin) {
        super(plugin, "lqa", "lq.admin");
        this.noPermissionLang(commandSender -> plugin.getLang().external("no-permission").asString());
        this.setSubCommands(
                new ReloadSub(plugin),
                new DeleteUserSub(plugin),
                new RefreshQuestsSub(plugin),
                new QuestIdsSub(plugin),
                new SubQuestIdsSub(plugin),
                new ResetQuestSub(plugin),
                new ProgressQuestSub(plugin),
                new MaterialBlockSub(plugin),
                new MaterialItemSub(plugin)
        );
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, "\n&cLuxury Quests &7by Hyfe and Zak Shearman\n"
                .concat("/lqa - This page.")
                .concat("/lqa reload - Reloads all the reloadable files.")
                .concat("/lqa quest ids <type> - List all of the quest ids and their names.")
                .concat("/lqa subquest ids <type> <quest id> - List a quests subquest ids and their names.")
                .concat("/lqa reset quest <player> <type> <id> - Resets a specific quest of a player.")
                .concat("/lqa refresh <type> - Refresh the quests of a quest type.")
                .concat("/lqa progress quest <player> <type> <quest id> <sub-quest id> <amount> - Progress a specific quest of a player.")
                .concat("/lqa material <block/item> - Get the config name of the item you're holding or block you're looking at.")
                .replace("- ", "&8- &7")
                .replace("/lqa", "&c/lqa")
                .replace(".", ".\n"));
    }
}
