package io.github.luxuryquests.commands.lq;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class HelpSub extends LqSubCommand<CommandSender> {

    public HelpSub(QuestsPlugin plugin) {
        super(plugin, true);
        this.addFlatWithAliases("help", "?");
    }

    @Override
    public void onExecute(CommandSender sender, String[] strings) {
        Text.sendMessage(sender, "\n&cLuxury Quests &7by Hyfe and Zak Shearman\n"
                .concat("/lq - Opens the portal menu.")
                .concat("/lq <menu> - Opens the specified menu.")
                .concat("/lq options - Opens the user options menu.")
                .concat("/lq standard - Opens the standard quests menu.")
                .concat("/lq daily - Opens the daily quests menu.")
                .concat("/lq weekly - Opens the weekly quests menu.")
                .concat("/lq rewards - Opens the unclaimed rewards menu.")
                .concat("/lq license - Gives information about the LuxuryQuests license on the server.")
                .concat("/lq info <player> - Gives information about the specified player.")
                .replace("- ", "&8- &7")
                .replace("/lq", "&c/lq")
                .replace(".", ".\n"));
    }
}
