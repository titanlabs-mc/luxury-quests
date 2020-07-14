package io.github.luxuryquests.commands.lq;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class LicenseSub extends LqSubCommand<CommandSender> {

    public LicenseSub(QuestsPlugin plugin) {
        super(plugin, true);
        this.addFlatWithAliases("license", "about", "info");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, "&7This server is running &cLuxuryQuests v".concat(this.plugin.getDescription().getVersion().concat(" &7by Hyfe and Zak Shearman"))
        .concat("\n\n&cUser ID:&f %%__USER__%%")
        .concat("\n&cDownload ID:&f %%__NONCE__%%"));
    }
}
