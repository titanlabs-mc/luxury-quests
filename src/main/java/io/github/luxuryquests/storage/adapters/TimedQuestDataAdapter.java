package io.github.luxuryquests.storage.adapters;

import com.google.gson.*;
import io.github.luxuryquests.objects.user.TimedQuestData;
import me.hyfe.simplespigot.storage.adapter.Adapter;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class TimedQuestDataAdapter implements Adapter<TimedQuestData> {

    @Override
    public TimedQuestData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String timeStartedString = jsonObject.get("timeStarted").getAsString();
        ZonedDateTime timeStarted = timeStartedString.equals("none") ? null : ZonedDateTime.parse(timeStartedString);
        int completions = jsonObject.get("completions").getAsInt();
        int attempts = jsonObject.get("attempts").getAsInt();
        return new TimedQuestData(timeStarted, completions, attempts);
    }

    @Override
    public JsonElement serialize(TimedQuestData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("timeStarted", src.getTimeStarted() == null ? "none" : src.getTimeStarted().toString());
        jsonObject.addProperty("completions", src.getCompletions());
        jsonObject.addProperty("attempts", src.getAttempts());
        return jsonObject;
    }
}
