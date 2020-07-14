package io.github.luxuryquests.config;

import com.google.common.collect.Maps;
import io.github.luxuryquests.QuestsPlugin;
import io.github.luxuryquests.objects.quest.Quest;
import io.github.luxuryquests.objects.quest.SubQuest;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Replace;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Lang {
    private final QuestsPlugin plugin;
    private final Map<String, String> localLang = Maps.newHashMap();
    private Map<String, Object> externalLang = Maps.newHashMap();

    public Lang(QuestsPlugin plugin) {
        this.plugin = plugin;
        this.localLang.put("invalid-refresh-type", "&7Could not find the refresh type &c%s&7.");
        this.localLang.put("successful-refresh", "&aSuccessfully&7 refreshed all the &a%s&7 quests.");
        this.localLang.put("invalid-quest-type", "&7Could not find the quest type &c%s&7.");
        this.localLang.put("quest-ids-title", "&cQuest id's and their names");
        this.localLang.put("quest-id", "&7   id: &c%s &7name: &c%s");
        this.localLang.put("invalid-quest-id", "&7Could not find the quest id &c%s&7 from the type &c%s&7. Use the command &c/lqa quest ids <type>&7.");
        this.localLang.put("invalid-subquest-id", "&7Could not find the sub-quest id &c%s&7 from the type &c%s&7. Use the command &c/lqa subquest ids <type> <quest id>&7.");
        this.localLang.put("subquest-ids-title", "&cSub-Quest id's and their names");
        this.localLang.put("subquest-id", "&7  id: &c%s &7name: &c%s");
        this.localLang.put("successful-reload", "&aSuccessfully&7 reloaded all files in &a%d&7 milliseconds.");
        this.localLang.put("successful-quest-reset", "&aSuccessfully&7 reset the quest &a%s&7 for &a%s&7.");
        this.localLang.put("failed-quest-reset", "&cFailed&7 to reset the quest &c%s&7 for &c%s&7.");
        this.localLang.put("successful-quest-progress", "&aSuccessfully&7 progressed the sub-quest &a%s&7 from the quest &a%s&7. ");
        this.localLang.put("user-data-deleted", "&aYou have successfully deleted %s's data.");
        this.localLang.put("target-user-data-deleted", "&cYour quest data has been wiped.");
        this.load();
    }

    public void load() {
        Config config = this.plugin.getConfig("lang");
        for (String key : config.keys("", false)) {
            this.externalLang.put(key, this.getCompatibleString(config, key));
        }
        for (String key : config.keys("quests", false)) {
            this.externalLang.put("quests.".concat(key), this.getCompatibleString(config, "quests.".concat(key)));
        }
    }

    public void reload() {
        this.externalLang.clear();
        this.load();
    }

    public LangSub of(String section) {
        return new LangSub(this.plugin.getConfig("lang").string(section));
    }

    public LangSub local(String id, Object... args) {
        return new LangSub(String.format(this.localLang.get(id), args));
    }

    public LangSub external(String id) {
        return this.external(id, null);
    }

    public LangSub external(String id, Replace replace) {
        Object requested = this.externalLang.get(id);
        if (requested == null) {
            System.out.println("^^^^^^^^^^ -[LuxuryQuests]- ^^^^^^^^^^");
            System.out.println(" ");
            System.out.println("Missing the configuration value '".concat(id).concat("', located in the file 'lang.yml'"));
            System.out.println(" ");
            System.out.println("^^^^^^^^^^ -[LuxuryQuests]- ^^^^^^^^^^");
            return null;
        }
        return new LangSub(Text.modify(String.valueOf(requested), replace));
    }

    public String questCompleteMessage(Quest quest) {
        return Text.modify(this.external("quests.base-message-completed").asString(), replacer -> replacer.set("quest_name", quest.getName()));
    }

    public String subQuestCompleteMessage(Quest quest, SubQuest subQuest) {
        return Text.modify(this.external("quests.base-message-sub-completed").asString(), replacer -> replacer
                .set("sub_quest_name", subQuest.getName())
                .set("quest_name", quest.getName()));
    }

    public String questProgressedMessage(Quest quest, int progress) {
        return Text.modify(this.external("quests.base-message-progressed").asString(), replacer -> replacer
                .set("quest_name", quest.getName())
                .set("progress", progress)
                .set("required_progress", quest.getRequiredProgress()));
    }

    public static class LangSub {
        private final String message;

        public LangSub(String message) {
            this.message = message;
        }

        public String asString() {
            return this.message;
        }

        public void to(CommandSender commandSender) {
            Text.sendMessage(commandSender, this.message);
        }
    }

    private String getCompatibleString(Config config, String key) {
        Object object = config.get(key);
        if (object instanceof String) {
            return String.valueOf(object);
        }
        StringBuilder builder = new StringBuilder();
        for (String line : config.stringList(key)) {
            builder.append(line)
            .append("\n");
        }
        return builder.toString();
    }
}
