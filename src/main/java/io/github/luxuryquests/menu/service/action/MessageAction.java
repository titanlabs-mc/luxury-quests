package io.github.luxuryquests.menu.service.action;

import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;

public class MessageAction extends Action {

    public MessageAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(Player player) {
        Text.sendMessage(player, this.value);
    }
}
