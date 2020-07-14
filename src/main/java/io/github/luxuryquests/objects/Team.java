package io.github.luxuryquests.objects;

import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.github.luxuryquests.objects.user.QuestStore;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Team {
    private Set<UUID> members = Sets.newHashSet();
    private QuestStore quests = new QuestStore();
    private Map<UUID, Multiset<String>> pendingRewards = Maps.newHashMap();

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public Set<UUID> getMembers() {
        return this.members;
    }

    public void addMember(UUID uuid) {
        this.members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        this.members.remove(uuid);
    }

    public QuestStore getQuests() {
        return this.quests;
    }

    public Map<UUID, Multiset<String>> getPendingRewards() {
        return this.pendingRewards;
    }
}
