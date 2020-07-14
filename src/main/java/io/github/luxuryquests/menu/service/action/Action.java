package io.github.luxuryquests.menu.service.action;

public abstract class Action {
    protected final String condition;
    protected final String value;

    public Action(String condition, String value) {
        this.condition = condition;
        this.value = value;
    }

    public static Action parse(String string) {
        String condition = string.contains("(") && string.contains(")") ? string.substring(string.indexOf("(") + 1, string.indexOf(")")).toLowerCase() : "";
        String value = string.contains("{") && string.contains("}") ? string.substring(string.indexOf("{") + 1, string.indexOf("}")) : "";
        switch (string.contains("[") && string.contains("]") ? string.substring(string.indexOf("[") + 1, string.indexOf("]")).toLowerCase() : "") {
            case "menu":
                return new MenuAction(condition, value);
            case "message":
                return new MessageAction(condition, value);
            case "sound":
                return new SoundAction(condition, value);
            default:
                return null;
        }
    }
}
