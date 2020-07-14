package io.github.luxuryquests.commands.lq;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.objects.user.User;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class InfoSub extends LqSubCommand<CommandSender> {

    public InfoSub(QuestsPlugin plugin) {
        super(plugin, true);
        this.addFlat("info");
        this.addArgument(User.class, "player");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 1);
        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[1])).to(sender);
            return;
        }
        this.lang.external("player-info", replacer -> {
            replacer.set("player", args[1]);
            return replacer;
        }).to(sender);
    }
}
