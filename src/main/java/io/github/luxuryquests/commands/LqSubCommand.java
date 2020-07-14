package io.github.luxuryquests.commands;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.config.Lang;
import me.hyfe.simplespigot.command.command.SubCommand;
import org.bukkit.command.CommandSender;

public abstract class LqSubCommand<T extends CommandSender> extends SubCommand<T> {
    protected final QuestsPlugin plugin;
    protected final Lang lang;

    public LqSubCommand(QuestsPlugin plugin, String permission, boolean isConsole) {
        super(plugin, permission, isConsole);
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    public LqSubCommand(QuestsPlugin plugin) {
        this(plugin, "", true);
    }

    public LqSubCommand(QuestsPlugin plugin, String permission) {
        this(plugin, permission, true);
    }

    public LqSubCommand(QuestsPlugin plugin, boolean isConsole) {
        this(plugin, "", isConsole);
    }
}
