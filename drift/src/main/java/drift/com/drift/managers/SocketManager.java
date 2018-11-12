package drift.com.drift.managers;


import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.phoenixframework.PhxChannel;
import org.phoenixframework.PhxMessage;
import org.phoenixframework.PhxSocket;

import java.util.Map;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Message;
import drift.com.drift.model.SocketAuth;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 * Created by eoin on 15/08/2017.
 */

public class SocketManager {

    private static String TAG = SocketManager.class.getSimpleName();

    private static SocketManager _socketManager = new SocketManager();

    private final Gson gson = APIManager.generateGson();

    private PhxSocket socket;
    private PhxChannel channel;

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

    public boolean isConnected(){

        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public void connect(final SocketAuth auth) {

        try {

            if (socket != null && socket.isConnected()) {
                socket.disconnect();
            }
            String url = getSocketURL(auth.orgId, auth.sessionToken);
            socket = new PhxSocket(url, null, new OkHttpClient());

            socket.setLogger(new Function1<String, Unit>() {
                @Override
                public Unit invoke(String s) {
                    LoggerHelper.logMessage("Drift Socket", s);
                    return Unit.INSTANCE;
                }
            });

            socket.onOpen(new Function0<Unit>() {
                @Override
                public Unit invoke() {

                    LoggerHelper.logMessage(TAG, "Connected");
                    channel = socket.channel("user:" + auth.userId, null);



                    channel.on("change", new Function1<PhxMessage, Unit>() {
                        @Override
                        public Unit invoke(PhxMessage phxMessage) {

                            LoggerHelper.logMessage(TAG, phxMessage.getEvent());

                            LoggerHelper.logMessage(TAG, phxMessage.getPayload().toString());

                            if (phxMessage.getPayload().containsKey("body")){

                                Object body = phxMessage.getPayload().get("body");
                                LoggerHelper.logMessage(TAG, "Body " + body.toString());

                                if (body instanceof Map<?, ?>){

                                    if (((Map) body).containsKey("object")){

                                        Object object = ((Map) body).get("object");
                                        LoggerHelper.logMessage(TAG, "Object " + object.toString());
                                        if (object instanceof Map<?, ?>) {

                                            if (((Map) object).containsKey("type") && ((Map) body).containsKey("data")) {
                                                Object type = ((Map) object).get("type");
                                                Object data = ((Map) body).get("data");


                                                LoggerHelper.logMessage(TAG, "Type " + type.toString());
                                                LoggerHelper.logMessage(TAG, "Data " + data.toString());
                                                if (type instanceof String && data instanceof Map<?,?>){

                                                    processEnvelopeData((String) type, (Map) data);
                                                    return Unit.INSTANCE;
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                            LoggerHelper.logMessage(TAG, "Failed to parse envelope! " + phxMessage.getPayload());

                            return Unit.INSTANCE;
                        }
                    });


                    channel.join(null, null)
                    .receive("ok", new Function1<PhxMessage, Unit>() {
                        @Override
                        public Unit invoke(PhxMessage phxMessage) {
                            LoggerHelper.logMessage(TAG, "You have joined '" + phxMessage.getEvent() + "'");

                            return Unit.INSTANCE;
                        }
                    });

                    return Unit.INSTANCE;
                }
            });

            socket.onClose(new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    LoggerHelper.logMessage(TAG, "Closed Socket");
                    return Unit.INSTANCE;
                }
            });


            socket.onError(new Function2<Throwable, Response, Unit>() {
                @Override
                public Unit invoke(Throwable throwable, Response response) {
                    throwable.printStackTrace();
                    LoggerHelper.logMessage(TAG, "Socket Error: " + response);


                    return Unit.INSTANCE;
                }
            });

            socket.connect();


        } catch (Exception e) {
            LoggerHelper.logMessage(TAG, "Failed to connect");
        }
    }

    private void processEnvelopeData(final String type, final Map<String, Object> data) {


        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                switch (type) {
                    case "MESSAGE":
                        JsonElement jsonData = gson.toJsonTree(data);

                        Message message = gson.fromJson(jsonData, Message.class);

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

    private String getSocketURL(int orgId, String socketAuth) {
        return "wss://" + String.valueOf(orgId) + "-" + String.valueOf(computeShardId(orgId)) + ".chat.api.drift.com/ws/websocket?session_token=" + socketAuth;
    }

    private static int computeShardId(int orgId){
        return orgId % 50; //WS_NUM_SHARDS
    }

}
