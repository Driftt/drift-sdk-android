package drift.com.drift.socket;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import drift.com.drift.helpers.LoggerHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class Socket {

    private static final String TAG = Socket.class.getSimpleName();

    public class PhoenixWSListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            LoggerHelper.logMessage(TAG, "WebSocket onOpen: {}");
            Socket.this.webSocket = webSocket;
            cancelReconnectTimer();

            startHeartbeatTimer();

            for (final ISocketOpenCallback callback : socketOpenCallbacks) {
                callback.onOpen();
            }

            Socket.this.flushSendBuffer();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
//            Log.d(TAG, "WebSocket onOpen: {} " + text);

            try {

                final Envelope envelope = gson.fromJson(text, Envelope.class);

//                final Envelope envelope = objectMapper.readValue(text, Envelope.class);
                synchronized (channels) {
                    for (final Channel channel : channels) {
                        if (channel.isMember(envelope.getTopic())) {
                            channel.trigger(envelope.getEvent(), envelope);
                        }
                    }
                }

                for (final IMessageCallback callback : messageCallbacks) {
                    callback.onMessage(envelope);
                }
            } catch (Throwable e) {
                LoggerHelper.logMessage(TAG, "Failed to read message payload");
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            onMessage(webSocket, bytes.toString());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            LoggerHelper.logMessage(TAG, "WebSocket onClose " + code + "/" + reason);
            Socket.this.webSocket = null;

            for (final ISocketCloseCallback callback : socketCloseCallbacks) {
                callback.onClose();
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            LoggerHelper.logMessage(TAG, "WebSocket connection error");
            try {
                //If there are multiple errorCallbacks do we really want to trigger
                //the same channel error callbacks multiple times?
                triggerChannelError();
                for (final IErrorCallback callback : errorCallbacks) {
                    callback.onError(t.getMessage());
                }
            } finally {
                // Assume closed on failure
                if (Socket.this.webSocket != null) {
                    try {
                        Socket.this.webSocket.close(1001 /*CLOSE_GOING_AWAY*/, "EOF received");
                    } finally {
                        Socket.this.webSocket = null;
                    }
                }
                if (reconnectOnFailure) {
                    scheduleReconnectTimer();
                }
            }
        }
    }

    public static final int RECONNECT_INTERVAL_MS = 5000;

    private static final int DEFAULT_HEARTBEAT_INTERVAL = 7000;

    private final List<Channel> channels = new ArrayList<>();

    private String endpointUri = null;

    private final Set<IErrorCallback> errorCallbacks = Collections.newSetFromMap(new HashMap<IErrorCallback, Boolean>());

    private final int heartbeatInterval;

    private TimerTask heartbeatTimerTask = null;

    private final OkHttpClient httpClient = new OkHttpClient();

    private final Set<IMessageCallback> messageCallbacks = Collections.newSetFromMap(new HashMap<IMessageCallback, Boolean>());

    private final Gson gson = new GsonBuilder().create();

    private boolean reconnectOnFailure = true;

    private TimerTask reconnectTimerTask = null;

    private int refNo = 1;

    private final LinkedBlockingQueue<RequestBody> sendBuffer = new LinkedBlockingQueue<>();

    private final Set<ISocketCloseCallback> socketCloseCallbacks = Collections
            .newSetFromMap(new HashMap<ISocketCloseCallback, Boolean>());

    private final Set<ISocketOpenCallback> socketOpenCallbacks = Collections
            .newSetFromMap(new HashMap<ISocketOpenCallback, Boolean>());

    private Timer timer = null;

    private WebSocket webSocket = null;

    /**
     * Annotated WS Endpoint. Private member to prevent confusion with "onConn*" registration
     * methods.
     */
    private final PhoenixWSListener wsListener = new PhoenixWSListener();

    public Socket(final String endpointUri) throws IOException {
        this(endpointUri, DEFAULT_HEARTBEAT_INTERVAL);
    }

    public Socket(final String endpointUri, final int heartbeatIntervalInMs) {
        LoggerHelper.logMessage(TAG, "PhoenixSocket(" + endpointUri + ")");
        this.endpointUri = endpointUri;
        this.heartbeatInterval = heartbeatIntervalInMs;
        this.timer = new Timer("Reconnect Timer for " + endpointUri);
    }

    /**
     * Retrieve a channel instance for the specified topic
     *
     * @param topic   The channel topic
     * @param payload The message payload
     * @return A Channel instance to be used for sending and receiving events for the topic
     */
    public Channel chan(final String topic, final JsonElement payload) {
        LoggerHelper.logMessage(TAG, "chan: " + topic + ", " + payload);
        final Channel channel = new Channel(topic, payload, Socket.this);
        synchronized (channels) {
            channels.add(channel);
        }
        return channel;
    }

    public void connect() throws IOException {
        LoggerHelper.logMessage(TAG, "connect");

        disconnect();
        // No support for ws:// or ws:// in okhttp. See https://github.com/square/okhttp/issues/1652
        final String httpUrl = this.endpointUri.replaceFirst("^ws:", "http:")
            .replaceFirst("^wss:", "https:");
        final Request request = new Request.Builder().url(httpUrl).build();
        webSocket = httpClient.newWebSocket(request, wsListener);
    }

    public void disconnect() throws IOException {
        LoggerHelper.logMessage(TAG, "disconnect");

        if (webSocket != null) {
            webSocket.close(1001 /*CLOSE_GOING_AWAY*/, "Disconnected by client");
        }
        cancelHeartbeatTimer();
        cancelReconnectTimer();
    }

    /**
     * @return true if the socket connection is connected
     */
    public boolean isConnected() {
        return webSocket != null;
    }

    /**
     * Register a callback for SocketEvent.ERROR events
     *
     * @param callback The callback to receive CLOSE events
     * @return This Socket instance
     */
    public Socket onClose(final ISocketCloseCallback callback) {
        this.socketCloseCallbacks.add(callback);
        return this;
    }

    /**
     * Register a callback for SocketEvent.ERROR events
     *
     * @param callback The callback to receive ERROR events
     * @return This Socket instance
     */
    public Socket onError(final IErrorCallback callback) {
        this.errorCallbacks.add(callback);
        return this;
    }

    /**
     * Register a callback for SocketEvent.MESSAGE events
     *
     * @param callback The callback to receive MESSAGE events
     * @return This Socket instance
     */
    public Socket onMessage(final IMessageCallback callback) {
        this.messageCallbacks.add(callback);
        return this;
    }

    /**
     * Register a callback for SocketEvent.OPEN events
     *
     * @param callback The callback to receive OPEN events
     * @return This Socket instance
     */
    public Socket onOpen(final ISocketOpenCallback callback) {
        cancelReconnectTimer();
        this.socketOpenCallbacks.add(callback);
        return this;
    }

    /**
     * Sends a message envelope on this socket
     *
     * @param envelope The message envelope
     * @return This socket instance
     * @throws IOException Thrown if the message cannot be sent
     */
    public Socket push(final Envelope envelope) throws IOException {

        String json = gson.toJson(envelope);

//        final JsonElement node = objectMapper.createObjectNode();
//        node.put("topic", envelope.getTopic());
//        node.put("event", envelope.getEvent());
//        node.put("ref", envelope.getRef());
//        node.set("payload", envelope.getPayload() == null ? objectMapper.createObjectNode() : envelope.getPayload());
//        final String json = objectMapper.writeValueAsString(node);

        LoggerHelper.logMessage(TAG, "push: " + envelope + ", isConnected:" + isConnected() + ", JSON: " + json);
        RequestBody body = RequestBody.create(MediaType.parse("text/xml"), json);

        if (this.isConnected()) {
            webSocket.send(json);
        } else {
            this.sendBuffer.add(body);
        }

        return this;
    }

    /**
     * Should the socket attempt to reconnect if websocket.onFailure is called.
     *
     * @param reconnectOnFailure reconnect value
     */
    public void reconectOnFailure(final boolean reconnectOnFailure) {
        this.reconnectOnFailure = reconnectOnFailure;
    }

    /**
     * Removes the specified channel if it is known to the socket
     *
     * @param channel The channel to be removed
     */
    public void remove(final Channel channel) {
        synchronized (channels) {
            for (final Iterator chanIter = channels.iterator(); chanIter.hasNext(); ) {
                if (chanIter.next() == channel) {
                    chanIter.remove();
                    break;
                }
            }
        }
    }

    Channel getChannel(String topic) {
        for (Channel channel : channels) {
            if (channel.getTopic().equalsIgnoreCase(topic)) {
                return channel;
            }
        }
        return null;
    }

    @Nullable
    public Channel getChannelOrCreate(String topic) {
        synchronized (channels) {
            Channel currentChannel = getChannel(topic);

            if (currentChannel != null) {
                ChannelState state = currentChannel.getState();
                if (state == ChannelState.JOINED || state == ChannelState.JOINING){
                    return null;
                } else {
                    remove(currentChannel);
                }
            } else {
                LoggerHelper.logMessage("SocketManager", "Channel Null: " + channels.size());
            }

            LoggerHelper.logMessage("SocketManager", "Channel Will Join: " + topic);

            return chan(topic, null);
        }
    }

    public void leaveAllChannels() {
        synchronized (channels) {
            try {

                for (Channel channel : channels) {
                    channel.leave();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "PhoenixSocket{" +
                "endpointUri='" + endpointUri + '\'' +
                ", channels(" + channels.size() + ")=" + channels +
                ", refNo=" + refNo +
                ", webSocket=" + webSocket +
                '}';
    }

    synchronized String makeRef() {
        int val = refNo++;
        if (refNo == Integer.MAX_VALUE) {
            refNo = 0;
        }
        return Integer.toString(val);
    }

    private void cancelHeartbeatTimer() {
        if (Socket.this.heartbeatTimerTask != null) {
            Socket.this.heartbeatTimerTask.cancel();
        }
    }

    private void cancelReconnectTimer() {
        if (Socket.this.reconnectTimerTask != null) {
            Socket.this.reconnectTimerTask.cancel();
        }
    }

    private void flushSendBuffer() {
        while (this.isConnected() && !this.sendBuffer.isEmpty()) {
            final RequestBody body = this.sendBuffer.remove();
            this.webSocket.send(body.toString());
        }
    }

    /**
     * Sets up and schedules a timer task to make repeated reconnect attempts at configured
     * intervals
     */
    private void scheduleReconnectTimer() {
        cancelReconnectTimer();
        cancelHeartbeatTimer();

        Socket.this.reconnectTimerTask = new TimerTask() {
            @Override
            public void run() {
                LoggerHelper.logMessage(TAG, "reconnectTimerTask run");
                try {
                    Socket.this.connect();
                } catch (Exception e) {
                    LoggerHelper.logMessage(TAG, "Failed to reconnect to");
                }
            }
        };
        try {
            timer.schedule(Socket.this.reconnectTimerTask, RECONNECT_INTERVAL_MS);
        } catch (Throwable t) {
            LoggerHelper.logMessage(TAG, "Failed to schedule reconnection timer: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void startHeartbeatTimer() {
        Socket.this.heartbeatTimerTask = new TimerTask() {
            @Override
            public void run() {
                LoggerHelper.logMessage(TAG, "heartbeatTimerTask run");
                if (Socket.this.isConnected()) {
                    try {
                        Envelope envelope = new Envelope("phoenix", "heartbeat",
                                new JsonObject(), Socket.this.makeRef());
                        Socket.this.push(envelope);
                    } catch (Exception e) {
                        LoggerHelper.logMessage(TAG, "Failed to send heartbeat");
                    }
                }
            }
        };

        timer.schedule(Socket.this.heartbeatTimerTask, Socket.this.heartbeatInterval,
            Socket.this.heartbeatInterval);
    }

    private void triggerChannelError() {
        synchronized (channels) {
            for (final Channel channel : channels) {
                channel.trigger(ChannelEvent.ERROR.getPhxEvent(), null);
            }
        }
    }

    static String replyEventName(final String ref) {
        return "chan_reply_" + ref;
    }
}
