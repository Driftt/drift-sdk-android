package drift.com.drift.socket;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Envelope {

    private String topic;


    private String event;

    @SerializedName("payload")
    private JsonElement payload = new JsonObject();

    private String ref;

    @SuppressWarnings("unused")
    public Envelope() {
    }

    public Envelope(final String topic, final String event, final JsonElement payload, final String ref) {
        this.topic = topic;
        this.event = event;
        if (payload != null) {
            this.payload = payload;
        }
        this.ref = ref;
    }

    public String getTopic() {
        return topic;
    }

    public String getEvent() {
        return event;
    }

    public JsonElement getPayload() {
        return payload;
    }

    /**
     * Helper to retrieve the value of "ref" from the payload
     *
     * @return The ref string or null if not found
     */
    public String getRef() {
        if (ref != null) return ref;

        if (payload.isJsonObject()) {
            JsonObject jsonObject = payload.getAsJsonObject();
            return jsonObject.get("ref").getAsString();
        }

        return null;
//        final JsonElement refNode = payload.get("ref");
//        return refNode != null ? refNode.textValue() : null;
    }

    /**
     * Helper to retrieve the value of "status" from the payload
     *
     * @return The status string or null if not found
     */
    public String getResponseStatus() {

        if (payload.isJsonObject()) {
            JsonObject jsonObject = payload.getAsJsonObject();
            return jsonObject.get("status").getAsString();
        }

        return null;

//        final JsonElement statusNode = payload.get("status");
//        return statusNode == null ? null : statusNode.textValue();
    }

    /**
     * Helper to retrieve the value of "reason" from the payload
     *
     * @return The reason string or null if not found
     */
    public String getReason() {

        if (payload.isJsonObject()) {
            JsonObject jsonObject = payload.getAsJsonObject();
            return jsonObject.get("reason").getAsString();
        }

        return null;

//        final JsonElement reasonNode = payload.get("reason");
//        return reasonNode == null ? null : reasonNode.textValue();
    }

    @Override
    public String toString() {
        return "Envelope{" +
            "topic='" + topic + '\'' +
            ", event='" + event + '\'' +
            ", payload=" + payload +
            '}';
    }
}
