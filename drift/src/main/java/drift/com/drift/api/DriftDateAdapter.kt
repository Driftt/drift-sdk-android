package drift.com.drift.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type
import java.util.Date


internal class DriftDateAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    @Synchronized
    override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(date.time)
    }

    @Synchronized
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Date {

        try {
            return Date(jsonElement.asLong)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        throw JsonParseException("Unparseable date: " + jsonElement.asString)
    }
}
