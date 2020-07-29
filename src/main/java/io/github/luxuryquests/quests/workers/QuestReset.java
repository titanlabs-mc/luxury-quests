package io.github.luxuryquests.quests.workers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.cache.QuestCache;
import io.github.luxuryquests.cache.UserCache;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.QuestType;
import io.github.luxuryquests.objects.quest.ResetType;
import io.github.luxuryquests.objects.user.User;
import io.github.luxuryquests.storage.ResetStorage;
import me.hyfe.simplespigot.scheduler.ThreadContext;
import me.hyfe.simplespigot.service.simple.Simple;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class QuestReset {
    private final QuestsPlugin plugin;
    private final String resetId;
    private final QuestType questType;
    private final ChronoUnit timeUnit;
    private final long unitInterval;
    private final int amount;
    private final boolean proxyMaster;
    private final ZoneId timeZone;
    private ZonedDateTime whenReset;

    protected final QuestCache questCache;
    protected final UserCache userCache;
    protected Set<Quest> currentQuests;

    public QuestReset(QuestsPlugin plugin, ResetType resetType, ChronoUnit timeUnit, long unitInterval, Set<Quest> currentQuests, Function<QuestReset, ZonedDateTime> whenReset) {
        this.plugin = plugin;
        this.resetId = resetType.getId();
        this.questType = resetType.getPartner();
        this.timeUnit = timeUnit;
        this.unitInterval = unitInterval;
        this.questCache = plugin.getQuestCache();
        this.userCache = plugin.getUserCache();
        this.currentQuests = currentQuests;
        this.amount = resetType.getAmount();
        this.proxyMaster = resetType.isProxyMaster();
        this.timeZone = resetType.getTimeZone();
        this.whenReset = this.parseTime((resetType.getUnitInterval() > 1 ? whenReset.apply(this) : this.now()).withSecond(0), resetType.getResetTime());
    }

    public QuestReset(QuestsPlugin plugin, ResetType resetType, ChronoUnit timeUnit, int unitInterval, Set<Quest> currentQuests) {
        this(plugin, resetType, timeUnit, unitInterval, currentQuests, QuestReset::now);
        this.reset();
    }

    public QuestType getQuestType() {
        return this.questType;
    }

    public ChronoUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long getUnitInterval() {
        return this.unitInterval;
    }

    public Set<Quest> getCurrentQuests() {
        return this.currentQuests;
    }

    public ZonedDateTime getWhenReset() {
        return this.whenReset;
    }

    public void start() {
        if (this.between() <= 0) {
            this.whenReset = this.whenReset.plus(this.unitInterval, this.timeUnit);
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.reset();
            this.start();
            for (User user : this.plugin.getUserCache().values()) {
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null && player.isOnline() && user.getBooleanOption(UserOptionType.RESET_NOTIFICATIONS)) {
                    this.plugin.getLang().external("quest-reset").to(player);
                }
            }
        }, this.between() * 20 + 40);
    }

    public void reset() {
        if (!this.proxyMaster) {
            this.plugin.wrappedScheduler().runDelay(ThreadContext.ASYNC, () -> {
                QuestReset questReset = this.plugin.getResetStorage().load(this.resetId);
                this.plugin.runSync(() -> this.currentQuests = questReset.getCurrentQuests());
            }, 200);
            return;
        }
        this.userCache.asyncModifyAll(user -> user.getQuests().asMap().put(this.questType.getName(), Maps.newHashMap()));
        this.currentQuests.clear();
        int max = Math.min(this.questCache.getQuests(this.questType).size(), this.amount);
        int iterations = 0;
        List<Quest> allQuests = Lists.newArrayList(this.questCache.getQuests(this.questType).values());
        Collections.shuffle(allQuests);
        for (Quest quest : allQuests) {
            if (iterations >= max) {
                break;
            }
            this.currentQuests.add(quest);
            iterations++;
        }
        this.plugin.runAsync(() -> this.plugin.getResetStorage().save(this.resetId, this));
    }

    public String asString() {
        return Simple.time().format(TimeUnit.SECONDS, ((int) this.between()));
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now().withZoneSameInstant(this.timeZone);
    }

    private long between() {
        return ChronoUnit.SECONDS.between(this.now(), this.whenReset);
    }

    private ZonedDateTime parseTime(ZonedDateTime date, String time) {
        String[] timeSplit = time.split(":");
        return date.withHour(StringUtils.isNumeric(timeSplit[0]) ? Integer.parseInt(timeSplit[0]) : 0).withMinute(timeSplit.length > 1 ? StringUtils.isNumeric(timeSplit[1]) ? Integer.parseInt(timeSplit[1]) : 0 : 0);
    }
}
