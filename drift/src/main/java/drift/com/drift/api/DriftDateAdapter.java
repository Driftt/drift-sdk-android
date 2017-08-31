package drift.com.drift.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;


class DriftDateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

    DriftDateAdapter() {

    }

    @Override
    public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(date.getTime());
    }

    @Override
    public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {

        try {
            return new Date(jsonElement.getAsLong());
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new JsonParseException("Unparseable date: " + jsonElement.getAsString());
    }
}
