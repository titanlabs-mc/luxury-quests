package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.objects.quest.ResetType;
import org.bukkit.command.CommandSender;

public class RefreshQuestsSub extends LqSubCommand<CommandSender> {

    public RefreshQuestsSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlat("refresh");
        this.addArgument(ResetType.class, "daily/weekly");
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        ResetType resetType = this.parseArgument(args, 1);

        if (resetType.equals(ResetType.UNKNOWN)) {
            this.lang.local("invalid-refresh-type", args[1]).to(commandSender);
            return;
        }
        resetType.getQuestReset().reset();
        this.lang.local("successful-refresh", args[1].toLowerCase()).to(commandSender);
    }
}
