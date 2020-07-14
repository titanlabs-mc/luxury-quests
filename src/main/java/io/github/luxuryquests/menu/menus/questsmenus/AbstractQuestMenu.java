package io.github.luxuryquests.menu.menus.questsmenus;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.StaticItemsCache;
import io.github.luxuryquests.config.Lang;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.menu.Lockable;
import io.github.luxuryquests.menu.UserDependent;
import io.github.luxuryquests.menu.service.extensions.PageableConfigMenu;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.TimedQuest;
import io.github.luxuryquests.objects.user.TimedQuestData;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Replace;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractQuestMenu extends PageableConfigMenu<Quest> implements UserDependent, Lockable {
    private final User user;
    private final StaticItemsCache staticItemsCache;
    private final QuestController controller;
    private final Lang lang;

    public AbstractQuestMenu(QuestsPlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
        this.staticItemsCache = plugin.getStaticItemsCache();
        this.controller = plugin.getQuestController();
        this.lang = plugin.getLang();
        this.addUpdater(plugin, 20);
    }

    public abstract Collection<Quest> quests();

    @Override
    public MenuItem pageableItem(Quest quest) {
        this.manipulateTimedQuest(quest);
        return this.equipItemsWithClicks(MenuItem.builderOf(Text.modify(this.getDynamicQuestItem(quest), this.applyReplacer(quest))), quest).build();
    }

    @Override
    public ImmutablePair<Collection<Quest>, Collection<Integer>> elementalValues() {
        return new ImmutablePair<>(this.quests(), MenuService.parseSlots(this, this.config, "quest-slots"));
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }

    @Override
    public boolean isLocked() {
        return this.config.bool("locked");
    }

    @Override
    public void show() {
        if (this.isLocked()) {
            return;
        }
        super.show();
    }

    private void manipulateTimedQuest(Quest quest) {
        this.controller.applyTimedQuestData(this.user, quest, (data, timedQuest) -> {
            if (data.isActive() && data.exceededTimeLimit(timedQuest)) {
                this.controller.failTimedMission(this.user, quest, data);
            }
            return null;
        });
    }

    private MenuItem.Builder equipItemsWithClicks(MenuItem.Builder builder, Quest quest) {
        MenuItem.Builder suppliedBuilder = this.controller.applyTimedQuest(quest, timedQuest -> {
            TimedQuestData questData = this.controller.getTimedQuestData(this.user, quest);
            return builder.onClick((menuItem, clickType) -> {
                if (Objects.nonNull(questData) && !questData.isActive() && questData.getCompletions() < timedQuest.getCompletionLimit()) {
                    questData.start();
                } else if (Objects.isNull(questData)) {
                    this.user.getQuests().createTimedQuestData((TimedQuest) quest).start();
                }
            });
        });
        return suppliedBuilder == null ? builder : suppliedBuilder;
    }

    private ItemStack getDynamicQuestItem(Quest quest) {
        boolean hasPermissions = this.userHasPermissions(quest.getRequiredPermissions());
        boolean requiredQuestsDone = this.controller.areRequiredQuestsDone(this.user, quest);
        Optional<ItemStack> notDoneItem = this.staticItemsCache.get("quest-not-unlocked-item");
        if (!hasPermissions) {
            if (!requiredQuestsDone) {
                return notDoneItem.orElseGet(quest::getItem);
            }
            Optional<ItemStack> noPermissionItem = this.staticItemsCache.get("quest-no-permission-item");
            return noPermissionItem.orElseGet(quest::getItem);
        }
        if (!requiredQuestsDone) {
            return notDoneItem.orElseGet(quest::getItem);
        }
        return quest.getItem();
    }

    private boolean userHasPermissions(Set<String> permissions) {
        if (permissions.isEmpty()) {
            return true;
        }
        Player player = Bukkit.getPlayer(this.user.getUuid());
        if (player == null) {
            return false;
        }
        for (String permission : permissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private Replace applyReplacer(Quest quest) {
        Replace replaceTimedQuest = this.controller.applyTimedQuest(quest, timedQuest -> replacer -> replacer
                .set("quest_name", quest.getItem().hasItemMeta() ? Text.decolorize(quest.getItem().getItemMeta().getDisplayName()) : quest.getName())
                .set("max_attempts", this.controller.applyTimedQuest(timedQuest, TimedQuest::getMaxAttempts))
                .set("completion_limit", this.controller.applyTimedQuest(timedQuest, TimedQuest::getCompletionLimit))
                .set("completions", this.controller.applyTimedQuestData(this.user, timedQuest, (data, ignore) -> data.getCompletions(), 0))
                .set("total_progress", this.controller.getQuestProgress(this.user, quest))
                .set("required_progress", quest.getRequiredProgress())
                .set("timed_status", this.controller.applyTimedQuestData(this.user, timedQuest, (timedQuestData, lambdaTimedQuest) ->
                        timedQuestData.status(lambdaTimedQuest, this.lang), this.lang.external("inactive").asString())));
        return Objects.nonNull(replaceTimedQuest) ? replaceTimedQuest : replacer -> replacer
                .set("quest_name", quest.getItem().hasItemMeta() ? Text.decolorize(quest.getItem().getItemMeta().getDisplayName()) : quest.getName())
                .set("total_progress", this.controller.getQuestProgress(this.user, quest))
                .set("required_progress", quest.getRequiredProgress());
    }
}
