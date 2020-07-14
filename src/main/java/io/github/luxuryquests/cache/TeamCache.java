package io.github.luxuryquests.cache;

import io.github.luxuryquests.objects.Team;
import me.hyfe.simplespigot.cache.FutureCache;
import me.hyfe.simplespigot.plugin.SimplePlugin;

import java.util.UUID;

public class TeamCache extends FutureCache<UUID, Team> {

    public TeamCache(SimplePlugin plugin) {
        super(plugin);
    }
}
