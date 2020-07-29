package io.github.luxuryquests.commands;

import io.github.luxuryquests.QuestsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AliasesListener implements Listener {
    private final List<String> lqAliases;

    public AliasesListener(QuestsPlugin plugin) {
        this.lqAliases = plugin.getConfig("settings").list("luxury-quests-aliases");
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String strippedCommand = event.getMessage().replace("/", "");
        String[] arguments = strippedCommand.split(" ");
        for (String alias : this.lqAliases) {
            if (arguments[0].equalsIgnoreCase(alias)) {
                event.setCancelled(true);
                String preparedArguments = arguments.length > 1 ? Arrays
                        .stream(arguments, 1, arguments.length)
                        .collect(Collectors.joining(" ")) : "";
                player.performCommand("luxuryquests ".concat(preparedArguments));
                break;
            }
        }
    }
}
