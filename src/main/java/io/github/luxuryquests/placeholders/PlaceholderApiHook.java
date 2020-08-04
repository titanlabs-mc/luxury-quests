package io.github.luxuryquests.placeholders;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.user.User;
import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Level;

public class PlaceholderApiHook extends PlaceholderExpansion {
    private UserCache userCache;
    private QuestCache questCache;
    private QuestController questController;

    public PlaceholderApiHook(QuestsPlugin plugin) {
        this.userCache = plugin.getUserCache();
        this.questCache = plugin.getQuestCache();
        this.questController = plugin.getQuestController();
    }

    @SneakyThrows
    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String placeholder) {
        if (placeholder.equals("test")) {
            return "successful";
        }
        if (offlinePlayer == null) {
            Bukkit.getLogger().log(Level.WARNING, "Could not get placeholder ".concat(placeholder).concat(" for user ").concat(FastUuid.toString(offlinePlayer.getUniqueId())).concat(" (player null)"));
            return "???";
        }
        Optional<User> optionalUser = this.userCache.getSync(offlinePlayer.getUniqueId());
        if (!optionalUser.isPresent()) {
            return "??? User not present";
        }
        User user = optionalUser.get();
        switch (placeholder) {
            case "completed":
                return String.valueOf(user.getAtomicLiveCompletedQuests().get());
        }
        if (placeholder.contains("_")) {
            String cleanedString = placeholder.substring(placeholder.indexOf("_") + 1);
            String[] arguments = cleanedString
                    .substring(cleanedString.indexOf("_") + 1).split("_"); // Gets everything after the PROGRESS_VALUE_ and puts it as variables.
            if (placeholder.startsWith("progress_percentage_")) {
                if (arguments.length > 2) {
                    return "Invalid Placeholder";
                }
                String category = arguments[0];
                if (arguments.length == 1) {
                    return String.valueOf(this.questController.getPercentageCategoryCompletion(user, category)).concat("%");
                }
                String questId = arguments[1];
                return this.questProgressPlaceholder(user, category, questId);
            }
            if (placeholder.startsWith("completed_")) {
                return String.valueOf(this.questController.calculateCompletedQuests(user, arguments[0]));
            }
        }

        return "Invalid Placeholder";
    }

    private String questProgressPlaceholder(User user, String category, String questId) {
        Quest quest = this.questCache.getQuest(category, questId);
        if (quest == null) {
            return "Invalid Quest";
        }
        return String.valueOf(this.questController.getQuestProgressPercentage(user, this.questCache.getQuest(category, questId))).concat("%");
    }

    @Override
    public @NotNull String getIdentifier() {
        return "luxuryquests";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hyfe/Zak";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public void reload(QuestsPlugin plugin) {
        this.userCache = plugin.getUserCache();
        this.questCache = plugin.getQuestCache();
        this.questController = plugin.getQuestController();
    }
}
