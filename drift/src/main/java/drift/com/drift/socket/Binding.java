package drift.com.drift.socket;


class Binding {
    private final String event;
    private final IMessageCallback callback;

    Binding(final String event, final IMessageCallback callback) {
        this.event = event;
        this.callback = callback;
    }

    public String getEvent() {
        return event;
    }

    public IMessageCallback getCallback() {
        return callback;
    }

    @Override
    public String toString() {
        return "Binding{" +
            "event='" + event + '\'' +
            ", callback=" + callback +
            '}';
    }
}
