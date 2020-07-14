package io.github.luxuryquests.objects.quest;

import me.hyfe.simplespigot.config.Config;

public class QuestType {
    private final String name;
    private final Config questsConfig;
    private final Config menuConfig;
    private boolean isStandard = true;

    public final static QuestType UNKNOWN = new QuestType("unknown", null, null);

    public QuestType(String name, Config questsConfig, Config menuConfig) {
        this.name = name;
        this.questsConfig = questsConfig;
        this.menuConfig = menuConfig;
    }

    public String getName() {
        return this.name.substring(0, 1) + this.name.substring(1).toLowerCase();
    }

    public Config getQuestsConfig() {
        return this.questsConfig;
    }

    public Config getMenuConfig() {
        return this.menuConfig;
    }

    public void toggleStandard() {
        this.isStandard = !this.isStandard;
    }

    public boolean isStandard() {
        return this.isStandard;
    }
}
