package io.github.luxuryquests.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public enum UserOptionType {

    UNKNOWN("unknown", "unknown"),
    AUTO_RECEIVE_REWARDS("auto-receive-rewards", "Auto Receive Rewards", "true", "false"),
    PROGRESS_NOTIFICATIONS("progress-notifications", "Progress Notifications", "action bar", "chat", "action bar & chat"),
    COMPLETION_NOTIFICATIONS("completion-notifications", "Quest Completion Notifications", "action bar", "chat", "action bar & chat"),
    RESET_NOTIFICATIONS("receive-reset-notifications", "Quest Reset Notifications", "true", "false");

    private final String id;
    private final String name;
    private final List<String> values;

    UserOptionType(String id, String name, String... values) {
        this.id = id;
        this.name = name;
        this.values = Lists.newArrayList(values);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }

    public String nextValue(String currentValue) {
        int currentIndex = this.values.indexOf(currentValue);
        return this.values.get(currentIndex + 2 > this.values.size() ? 0 : currentIndex + 1);
    }

    public static UserOptionType parse(String id) {
        for (UserOptionType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public static Map<UserOptionType, String> parseUserOptions(Map<String, String> userOptions) {
        Map<UserOptionType, String> resultUserOptions = Maps.newEnumMap(UserOptionType.class);
        for (Map.Entry<String, String> entry : userOptions.entrySet()) {
            UserOptionType type = parse(entry.getKey());
            if (!type.equals(UNKNOWN)) {
                resultUserOptions.put(type, entry.getValue());
            }
        }
        return resultUserOptions;
    }

    public static Map<String, String> toSerializableUserOptions(Map<UserOptionType, String> userOptions) {
        Map<String, String> resultUserOptions = Maps.newHashMap();
        for (Map.Entry<UserOptionType, String> entry : userOptions.entrySet()) {
            resultUserOptions.put(entry.getKey().id, entry.getValue());
        }
        return resultUserOptions;
    }
}
