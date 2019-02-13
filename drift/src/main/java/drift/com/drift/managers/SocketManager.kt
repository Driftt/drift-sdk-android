package drift.com.drift.managers


import android.os.Handler
import android.os.Looper
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.model.Message
import drift.com.drift.model.SocketAuth
import okhttp3.OkHttpClient


/**
 * Created by eoin on 15/08/2017.
 */

internal object SocketManager {

    private val TAG = SocketManager::class.java.simpleName

    private val gson = APIManager.generateGson()

    private var socket: PhxSocket? = null
    private var channel: PhxChannel? = null

    val isConnected: Boolean
        get() = socket?.isConnected ?: false

    fun disconnect() {
        try {

            if (socket?.isConnected == true) {
                socket?.disconnect()
            }

        } catch (e: Exception) {
            LoggerHelper.logMessage(TAG, "Failed to disconnect")
        }

    }

    fun connect(auth: SocketAuth) {

        try {

            if (socket?.isConnected == true) {
                socket?.disconnect()
            }
            val url = getSocketURL(auth.orgId, auth.sessionToken)
            socket = PhxSocket(url, null, OkHttpClient())

            socket?.logger = {
                LoggerHelper.logMessage("Drift Socket", it)
            }

            socket?.onOpen {
                LoggerHelper.logMessage(TAG, "Connected")
                channel = socket?.channel("user:" + auth.userId!!, null)



                channel?.on("change") { phxMessage ->
                    LoggerHelper.logMessage(TAG, phxMessage.event)

                    LoggerHelper.logMessage(TAG, phxMessage.payload.toString())

                    if (phxMessage.payload.containsKey("body")) {

                        val body = phxMessage.payload["body"]
                        LoggerHelper.logMessage(TAG, "Body " + body!!.toString())

                        if (body is Map<*, *>) {

                            if (body.containsKey("object")) {

                                val `object` = body["object"]
                                LoggerHelper.logMessage(TAG, "Object " + `object`!!.toString())
                                if (`object` is Map<*, *>) {

                                    if (`object`.containsKey("type") && body.containsKey("data")) {
                                        val type = `object`["type"]
                                        val data = body["data"]


                                        LoggerHelper.logMessage(TAG, "Type " + type!!.toString())
                                        LoggerHelper.logMessage(TAG, "Data " + data!!.toString())
                                        if (type is String && data is Map<*, *>) {
                                            processEnvelopeData(type, data as Map<String, Any>)
                                            return@on Unit
                                        }

                                    }
                                }
                            }
                        }
                    }
                    LoggerHelper.logMessage(TAG, "Failed to parse envelope! " + phxMessage.payload)
                }


                channel?.join(null, null)
                        ?.receive("ok") { phxMessage ->
                            LoggerHelper.logMessage(TAG, "You have joined '" + phxMessage.event + "'")
                        }
            }

            socket?.onClose {
                LoggerHelper.logMessage(TAG, "Closed Socket")
            }

            socket?.onError { throwable, response ->
                throwable?.printStackTrace()
                LoggerHelper.logMessage(TAG, "Socket Error: $response")
            }

            socket?.connect()


        } catch (e: Exception) {
            LoggerHelper.logMessage(TAG, "Failed to connect")
        }

    }

    private fun processEnvelopeData(type: String, data: Map<String, Any>) {


        val mainHandler = Handler(Looper.getMainLooper())

        val myRunnable = Runnable {
            when (type) {
                "MESSAGE" -> {
                    val jsonData = gson.toJsonTree(data)

                    val message = gson.fromJson(jsonData, Message::class.java)

                    if (message != null && message.contentType == "CHAT") {
                        LoggerHelper.logMessage(TAG, "Received new Message")
                        PresentationManager.didReceiveNewMessage(message)
                    }
                }
                else -> LoggerHelper.logMessage(TAG, "Undealt with socket event type: $type")
            }
        }
        mainHandler.post(myRunnable)
    }

    private fun getSocketURL(orgId: Int, socketAuth: String?): String {
        return "wss://" + orgId.toString() + "-" + computeShardId(orgId).toString() + ".chat.api.drift.com/ws/websocket?session_token=" + socketAuth
    }

    private fun computeShardId(orgId: Int): Int {
        return orgId % 50 //WS_NUM_SHARDS
    }
}
