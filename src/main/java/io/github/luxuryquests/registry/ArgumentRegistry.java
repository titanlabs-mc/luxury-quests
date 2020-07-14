package io.github.luxuryquests.registry;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.loaders.TypeLoader;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.ResetType;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.command.CommandBase;
import me.hyfe.simplespigot.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ArgumentRegistry implements Registry {
    private final CommandBase commandBase;
    private final UserCache userCache;

    public ArgumentRegistry(QuestsPlugin plugin) {
        this.commandBase = plugin.getCommandBase();
        this.userCache = plugin.getUserCache();
    }

    @Override
    public void register() {
        this.commandBase
                .registerArgumentType(User.class, string -> {
                    Player player = Bukkit.getPlayerExact(string);
                    return player == null ? Optional.empty() : this.userCache.getSync(player.getUniqueId());
                })
                .registerArgumentType(ResetType.class, TypeLoader::parseResetType)
                .registerArgumentType(QuestType.class, TypeLoader::parseQuestType);
    }
}
