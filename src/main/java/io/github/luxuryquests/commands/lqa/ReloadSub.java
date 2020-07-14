package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.commands.LqSubCommand;
import org.bukkit.command.CommandSender;

public class ReloadSub extends LqSubCommand<CommandSender> {

    public ReloadSub(QuestsPlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlat("reload");
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        long whenStart = System.currentTimeMillis();
        this.plugin.reload();
        this.lang.local("successful-reload", System.currentTimeMillis() - whenStart).to(commandSender);
    }
}
