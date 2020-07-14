package io.github.luxuryquests.objects.reward;

import com.google.common.collect.Multiset;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.menu.service.action.Action;
import io.github.luxuryquests.menu.service.action.MenuAction;
import io.github.luxuryquests.menu.service.action.MessageAction;
import io.github.luxuryquests.menu.service.action.SoundAction;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class Reward<T>{
    private final String id;
    private final String name;
    private final List<String> loreAddon;
    private final Set<Action> actions;
    protected Multiset<T> set;

    public Reward(String id, String name, List<String> loreAddon, Set<Action> actions, Multiset<T> set) {
        this.id = id;
        this.name = name;
        this.loreAddon = loreAddon;
        this.actions = actions;
        this.set = set;
    }

    public abstract void reward(QuestsPlugin plugin, Player player);

    public void runActions(QuestsPlugin plugin, Player player) {
        for (Action action : this.actions) {
            if (action instanceof MenuAction) {
                ((MenuAction) action).accept(plugin.getMenuFactory(), null, player);
            } else if (action instanceof MessageAction) {
                ((MessageAction) action).accept(player);
            } else if (action instanceof SoundAction) {
                ((SoundAction) action).accept(player);
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLoreAddon() {
        return this.loreAddon;
    }

    public Multiset<T> getSet() {
        return this.set;
    }

    public void setSet(Multiset<T> set) {
        this.set = set;
    }

    public void addElement(T element) {
        this.set.add(element);
    }

    public void removeElement(T element) {
        this.set.remove(element);
    }
}
