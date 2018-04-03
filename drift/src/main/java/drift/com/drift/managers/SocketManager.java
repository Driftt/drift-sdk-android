package drift.com.drift.managers;


import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Message;
import drift.com.drift.model.SocketAuth;
import drift.com.drift.socket.Channel;
import drift.com.drift.socket.Envelope;
import drift.com.drift.socket.IErrorCallback;
import drift.com.drift.socket.IMessageCallback;
import drift.com.drift.socket.ISocketCloseCallback;
import drift.com.drift.socket.ISocketOpenCallback;
import drift.com.drift.socket.Socket;


/**
 * Created by eoin on 15/08/2017.
 */

public class SocketManager {

    private static String TAG = SocketManager.class.getSimpleName();

    private static SocketManager _socketManager = new SocketManager();

    private final Gson gson = APIManager.generateGson();

    private Socket socket;
    private Channel channel;

    public static SocketManager getInstance() {
        return _socketManager;
    }

    public void disconnect() {
        try {

            if (socket != null && socket.isConnected()) {
                socket.disconnect();
            }

        } catch (Exception e) {
            LoggerHelper.logMessage(TAG, "Failed to disconnect");
        }
    }

    public void connect(final SocketAuth auth) {

        try {

            if (socket != null && socket.isConnected()) {
                socket.disconnect();
            }

            socket = new Socket("wss://chat.api.drift.com/ws/websocket?session_token=" + auth.sessionToken);

            socket.onOpen(new ISocketOpenCallback() {
                @Override
                public void onOpen() {
                    LoggerHelper.logMessage(TAG, "Connected");
                    channel = socket.chan("user:" + auth.userId, null);

                    try {
                        channel.join().receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {
                                LoggerHelper.logMessage(TAG, "You have joined '" + envelope.getEvent() + "'");
                            }
                        });
                        channel.on("change", new IMessageCallback() {
                            @Override
                            public void onMessage(final Envelope envelope) {

                                LoggerHelper.logMessage(TAG, envelope.getEvent());

                                if (envelope.getPayload().isJsonObject()) {
                                    JsonObject payload = envelope.getPayload().getAsJsonObject();
                                    JsonElement bodyElement = payload.get("body");
                                    if (bodyElement.isJsonObject()) {
                                        JsonObject body = bodyElement.getAsJsonObject();
                                        JsonElement objectElement = body.get("object");
                                        if (objectElement.isJsonObject()) {
                                            JsonObject object = objectElement.getAsJsonObject();
                                            JsonElement typeElement = object.get("type");
                                            String type = typeElement.getAsString();

                                            JsonElement dataElement = body.get("data");
                                            if (dataElement.isJsonObject()) {
                                                JsonObject data = dataElement.getAsJsonObject();
                                                processEnvelopeData(type, data);
                                                return;
                                            }
                                        }
                                    }
                                }

                                LoggerHelper.logMessage(TAG, "Failed to parse envelope! " + envelope.getPayload());
                            }
                        });
                    } catch (Exception e) {
                        LoggerHelper.logMessage(TAG, "Failed to join channel ");
                    }

                }
            })
            .onClose(new ISocketCloseCallback() {
                @Override
                public void onClose() {
                    LoggerHelper.logMessage(TAG, "Closed Socket");
                }
            })
            .onError(new IErrorCallback() {
                @Override
                public void onError(final String reason) {
//                    handleTerminalError(reason);
                    LoggerHelper.logMessage(TAG, "Socket Error");

                }
            })
            .connect();

        } catch (Exception e) {
            LoggerHelper.logMessage(TAG, "Failed to connect");
        }
    }

    private void processEnvelopeData(final String type, final JsonObject data) {


        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                switch (type) {
                    case "MESSAGE":
                        Message message = gson.fromJson(data, Message.class);

                        if (message != null && message.contentType.equals("CHAT")) {
                            LoggerHelper.logMessage(TAG, "Received new Message");
                            PresentationManager.getInstance().didReceiveNewMessage(message);
                        }

                        break;
                    default:
                        LoggerHelper.logMessage(TAG, "Undealt with socket event type: " + type);
                        break;
                }

            }
        };
        mainHandler.post(myRunnable);
    }

}
