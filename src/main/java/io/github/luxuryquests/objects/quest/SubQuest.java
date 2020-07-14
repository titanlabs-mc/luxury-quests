package io.github.luxuryquests.objects.quest;

import io.github.luxuryquests.objects.quest.variable.Variable;

import java.util.Map;

public class SubQuest {
    private final String id;
    private final String name;
    private final String type;
    private final Variable variable;
    private final int requiredProgress;
    private final boolean antiAbuse;
    private final Map<String, Integer> categoryRequirements;

    public SubQuest(String id, String name, String type, Variable variable, int requiredProgress, boolean antiAbuse, Map<String, Integer> categoryRequirements) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.variable = variable;
        this.requiredProgress = requiredProgress;
        this.antiAbuse = antiAbuse;
        this.categoryRequirements = categoryRequirements;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public int getRequiredProgress() {
        return this.requiredProgress;
    }

    public boolean isAntiAbuse() {
        return this.antiAbuse;
    }

    public Map<String, Integer> getCategoryRequirements() {
        return this.categoryRequirements;
    }
}
