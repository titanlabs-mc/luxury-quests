package io.github.luxuryquests.quests.quests.external;

import ga.strikepractice.events.*;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class StrikePracticeQuests extends ExternalQuestExecutor {

    public StrikePracticeQuests(QuestsPlugin plugin) {
        super(plugin, "strikepractice");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHost(PlayerHostEvent event) {
        if (!(event.getHost() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getHost();
        String eventName = event.getEventName();

        this.execute("host_events", player, result -> result.root(eventName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBotDuelStart(BotDuelStartEvent event) {
        Player player =event.getPlayer();
        String kitName = event.getFight().getKit().getName();
        String arenaName = event.getFight().getArena().getName();

        this.execute("play_games", player, result -> {
            result.subRoot("opponent", "none");
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });

        this.execute("play_bot_games", player, result -> {
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onBotDuelEnd(BotDuelEndEvent event) {
        if (event.getWinner().equals(event.getBot().getFullName())) {
            return;
        }
        Player winner = Bukkit.getPlayer(event.getWinner());
        String kitName = event.getFight().getKit().getName();
        String arenaName = event.getFight().getArena().getName();

        this.execute("win_games", winner, result -> {
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });

        this.execute("win_bot_games", winner, result -> {
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onDuelStart(DuelStartEvent event) {
        Player player1 = event.getPlayer1();
        Player player2 = event.getPlayer2();
        String kitName = event.getKit().getName();
        String arenaName = event.getFight().getArena().getName();

        this.execute("play_games", player2, result -> {
            result.subRoot("opponent", player1.getName());
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });

        this.execute("play_games", player1, result -> {
            result.subRoot("opponent", player2.getName());
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onDuelEnd(DuelEndEvent event) {
        Player loser = event.getLoser();
        Player winner = event.getWinner();
        String arenaName = event.getFight().getArena().getName();
        String kitName = event.getFight().getKit().getName();

        this.execute("win_games", winner, result -> {
            result.subRoot("loser", loser.getName());
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });

        this.execute("lose_games", winner, result -> {
            result.subRoot("opponent", winner.getName());
            result.subRoot("arena-name", arenaName);
            return result.root(kitName);
        });
    }
}
