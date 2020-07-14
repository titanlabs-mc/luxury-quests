package io.github.luxuryquests.commands.lqa;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.commands.LqSubCommand;
import io.github.luxuryquests.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeleteUserSub extends LqSubCommand<CommandSender> {
    private final UserCache userCache;

    public DeleteUserSub(QuestsPlugin plugin) {
        super(plugin);
        this.userCache = plugin.getUserCache();
        this.inheritPermission();
        this.addFlats("delete", "user");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        UUID uuid = maybeUser.get().getUuid();
        Player player = Bukkit.getPlayer(uuid);
        this.userCache.invalidate(uuid);
        this.userCache.set(uuid, new User(uuid, this.userCache.getDefaultUserOptions()));
        this.lang.local("target-user-data-deleted").to(player);
        if (player != null && !sender.getName().equals(player.getName())) {
            this.lang.local("user-data-deleted", player.getName()).to(sender);
        }
    }
}
