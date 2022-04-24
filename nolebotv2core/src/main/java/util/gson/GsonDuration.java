package util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

public class GsonDuration implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(
            final JsonElement jsonElement,
            final Type type,
            final JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        final String durationString = jsonElement.getAsString();

        return Duration.parse(durationString);
    }

    @Override
    public JsonElement serialize(
            final Duration duration,
            final Type type,
            final JsonSerializationContext jsonSerializationContext
    ) {
        return new JsonPrimitive(duration.toString());
    }
}