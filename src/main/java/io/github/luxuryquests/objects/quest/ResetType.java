package io.github.luxuryquests.objects.quest;

import io.github.luxuryquests.quests.workers.QuestReset;
import me.hyfe.simplespigot.config.Config;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class ResetType {
    private final String id;
    private final ChronoUnit timeUnit;
    private final int unitInterval;
    private final int amount;
    private final ZoneId timeZone;
    private final String resetTime;
    private final boolean proxyMaster;
    private final QuestType partner;
    private final Function<String, QuestReset> questReset;

    public final static ResetType UNKNOWN = new ResetType();

    public ResetType() {
        this.id = "z";
        this.timeUnit = null;
        this.unitInterval = -1;
        this.amount = -1;
        this.timeZone = null;
        this.resetTime = null;
        this.proxyMaster = true;
        this.partner = null;
        this.questReset = null;
    }

    public ResetType(String id, Config config, QuestType partner, Function<String, QuestReset> questReset) {
        this.id = id;
        this.timeUnit = ChronoUnit.valueOf(config.string("settings.time-unit") == null ? "DAYS" : config.string("settings.time-unit"));
        this.unitInterval = config.integer("settings.reset-interval");
        this.amount = config.integer("settings.quest-amount");
        this.timeZone = ZoneId.of(config.string("settings.timezone"));
        this.resetTime = config.string("settings.reset-time");
        this.proxyMaster = !config.has("settings.master-proxy-sync") || config.bool("settings.master-proxy-sync");
        this.partner = partner;
        this.questReset = questReset;
    }

    public String getId() {
        return this.id;
    }

    public ChronoUnit getTimeUnit() {
        return this.timeUnit;
    }

    public int getUnitInterval() {
        return this.unitInterval;
    }

    public int getAmount() {
        return this.amount;
    }

    public ZoneId getTimeZone() {
        return this.timeZone;
    }

    public String getResetTime() {
        return this.resetTime;
    }

    public QuestType getPartner() {
        return this.partner;
    }

    public boolean isProxyMaster() {
        return this.proxyMaster;
    }

    public QuestReset getQuestReset() {
        return this.questReset.apply(this.id);
    }
}
