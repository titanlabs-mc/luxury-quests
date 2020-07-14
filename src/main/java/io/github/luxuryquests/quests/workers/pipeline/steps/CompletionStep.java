package io.github.luxuryquests.quests.workers.pipeline.steps;

import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.config.Lang;
import io.github.luxuryquests.controller.QuestController;
import io.github.luxuryquests.enums.UserOptionType;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.SubQuest;
import io.github.luxuryquests.objects.user.User;
import me.hyfe.simplespigot.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class CompletionStep {
    private final RewardStep rewardStep;
    private final QuestController controller;
    private final Lang lang;

    public CompletionStep(QuestsPlugin plugin) {
        this.rewardStep = new RewardStep(plugin);
        this.controller = plugin.getQuestController();
        this.lang = plugin.getLang();
    }

    public void process(User user, Quest quest, SubQuest subQuest, int totalProgress, int progressIncrement, boolean overrideUpdate) {
            Optional<Player> maybePlayer = Optional.ofNullable(Bukkit.getPlayer(user.getUuid()));
            int updatedProgress = overrideUpdate ?
                    this.controller.setSubQuestProgress(user, quest, subQuest, Math.min(progressIncrement, subQuest.getRequiredProgress()))
                    : this.controller.addSubQuestProgress(user, quest, subQuest, Math.min(progressIncrement, subQuest.getRequiredProgress()));
            for (int notifyAt : quest.getNotifyAt()) {
                if (updatedProgress == notifyAt || (notifyAt > totalProgress && notifyAt < updatedProgress)) {
                    String message = this.lang.questProgressedMessage(quest, this.controller.getQuestProgress(user, quest));
                    String value = user.getOptionValue(UserOptionType.PROGRESS_NOTIFICATIONS).toLowerCase();
                    maybePlayer.ifPresent(player -> {
                        if (value.contains("action bar")) {
                            this.sendActionBar(player, message);
                        }
                        if (value.contains("chat")) {
                            Text.sendMessage(player, message);
                        }
                    });
                    break;
                }
            }
            if (updatedProgress >= subQuest.getRequiredProgress() && quest.getSubQuests().size() > 1) {
                String message = this.lang.subQuestCompleteMessage(quest, subQuest);
                String value = user.getOptionValue(UserOptionType.COMPLETION_NOTIFICATIONS);
                maybePlayer.ifPresent(player -> this.sendCompletionNotifications(player, value, message));
            }
            if (this.controller.isQuestDone(user, quest)) {
                this.controller.applyTimedQuestData(user, quest, (data, timedQuest) -> {
                    data.updateCompletions(completions -> completions + 1);
                    data.updateAttempts(attempts -> attempts + 1);
                    if (data.getCompletions() < timedQuest.getCompletionLimit() || data.getAttempts() < timedQuest.getMaxAttempts()) {
                        this.controller.failTimedMission(user, quest, data);
                    }
                    return null;
                });
                user.getAtomicLiveCompletedQuests().getAndIncrement();
                user.getPersistentCompletedQuests().getAndIncrement();
                if (updatedProgress >= subQuest.getRequiredProgress()) {
                    String message = this.lang.questCompleteMessage(quest);
                    String value = user.getOptionValue(UserOptionType.COMPLETION_NOTIFICATIONS).toLowerCase();
                    maybePlayer.ifPresent(player -> this.sendCompletionNotifications(player, value, message));
                }
                this.rewardStep.process(user, quest);
            }
    }

    private void sendCompletionNotifications(Player player, String value, String message) {
        if (value.contains("action bar")) {
            this.sendActionBar(player, message);
        }
        if (value.contains("chat")) {
            Text.sendMessage(player, message);
        }
    }

    // Credits to Benz
    private void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
