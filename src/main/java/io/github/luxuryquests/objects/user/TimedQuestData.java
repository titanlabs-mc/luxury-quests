package io.github.luxuryquests.objects.user;

import io.github.luxuryquests.config.Lang;
import io.github.luxuryquests.objects.quest.TimedQuest;
import me.hyfe.simplespigot.service.simple.Simple;
import me.hyfe.simplespigot.service.simple.services.TimeService;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class TimedQuestData {
    private ZonedDateTime timeStarted;
    private int completions;
    private int attempts;
    private boolean pendingAttempt;

    public TimedQuestData() {
        this.completions = 0;
        this.attempts = 0;
    }

    public TimedQuestData(ZonedDateTime timeStarted, int completions, int attempts) {
        this.timeStarted = timeStarted;
        this.completions = completions;
        this.attempts = attempts;
    }

    public ZonedDateTime getTimeStarted() {
        return this.timeStarted;
    }

    public int getCompletions() {
        return this.completions;
    }

    public void updateCompletions(UnaryOperator<Integer> completions) {
        this.completions = completions.apply(this.completions);
    }

    public int getAttempts() {
        return this.attempts;
    }

    public void updateAttempts(UnaryOperator<Integer> attempts) {
        this.attempts = attempts.apply(this.attempts);
    }

    public void start() {
        this.timeStarted = ZonedDateTime.now();
        this.pendingAttempt = true;
    }

    public void stop() {
        this.timeStarted = null;
    }

    public boolean isActive() {
        return this.timeStarted != null;
    }

    public boolean exceededTimeLimit(TimedQuest timedQuest) {
        return ChronoUnit.SECONDS.between(this.timeStarted, ZonedDateTime.now()) > timedQuest.getTimeToComplete();
    }

    public String status(TimedQuest timedQuest, Lang lang) {
        if (this.pendingAttempt && !this.isActive()) {
            this.attempts++;
            this.pendingAttempt = false;
        }
        if (this.completions >= timedQuest.getCompletionLimit()) {
            return lang.external("completed").asString();
        }
        if (this.attempts >= timedQuest.getMaxAttempts()) {
            return lang.external("no-attempts").asString();
        }
        if (!this.isActive()) {
            return lang.external("inactive").asString();
        }
        return Simple.time().format(TimeUnit.SECONDS, timedQuest.getTimeToComplete() - ChronoUnit.SECONDS.between(this.timeStarted, ZonedDateTime.now()));
    }
}
