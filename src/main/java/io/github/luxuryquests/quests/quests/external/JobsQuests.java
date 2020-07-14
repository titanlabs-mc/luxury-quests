package io.github.luxuryquests.quests.quests.external;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class JobsQuests extends ExternalQuestExecutor {

    public JobsQuests(QuestsPlugin plugin) {
        super(plugin, "jobs");
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobJoin(JobsJoinEvent event) {
        Player player = event.getPlayer().getPlayer();
        String jobName = event.getJob().getName();

        this.execute("join", player, result -> result.root(jobName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onExpGain(JobsExpGainEvent event) {
        Player player = event.getPlayer().getPlayer();
        String jobName = event.getJob().getName();
        int gainedExperience = (int) event.getExp();

        this.execute("gain_exp", player, gainedExperience, result -> result.root(jobName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(JobsLevelUpEvent event) {
        Player player = event.getPlayer().getPlayer();
        String jobName = event.getJobName();
        int level = event.getLevel();

        this.execute("level_up", player, level, result -> result.root(jobName), replacer -> replacer, true);
    }
}
