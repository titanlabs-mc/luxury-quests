package io.github.luxuryquests.cache.listener;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.config.Lang;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class ConnectionListener implements Listener {
    private final QuestsPlugin plugin;
    private final UserCache userCache;
    private final Lang lang;
    private final boolean bungeeFix;

    public ConnectionListener(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getUserCache();
        this.lang = plugin.getLang();
        this.bungeeFix = plugin.getConfig("settings").bool("storage-options.bungee-fix");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.bungeeFix) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                this.loadPlayer(player);
            }, 10);
        } else {
            this.loadPlayer(player);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.userCache.unload(event.getPlayer().getUniqueId(), true);
    }

    private void loadPlayer(Player player) {
        CompletableFuture<User> completableUser = this.userCache.load(player.getUniqueId());
        completableUser.thenAccept(user -> {
            if (!user.getPendingRewardIds().isEmpty()) {
                this.messageDelay(() -> {
                    this.lang.external("collectable-rewards-notification", replacer -> replacer.set("player", player.getName())).to(player);
                });
            }
        });
    }

    private void messageDelay(Runnable runnable) {
        Config settings = this.plugin.getConfig("settings");
        if (settings.has("join-message-delay")) {
            int delay = settings.integer("join-message-delay");
            Bukkit.getScheduler().runTaskLater(this.plugin, runnable, 20 * delay);
        } else {
            runnable.run();
        }
    }
}
