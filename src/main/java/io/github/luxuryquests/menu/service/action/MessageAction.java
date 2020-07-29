package io.github.luxuryquests.menu.service.action;

import me.hyfe.simplespigot.annotations.Nullable;
import me.hyfe.simplespigot.text.Replacer;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;

public class MessageAction extends Action {

    public MessageAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(Player player, @Nullable Replacer replacer) {
        Text.sendMessage(player, replacer == null ? this.value : replacer.applyTo(this.value));
    }
}
