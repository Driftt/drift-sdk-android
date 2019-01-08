package drift.com.drift.wrappers

import android.text.Html
import android.text.SpannableStringBuilder
import android.text.util.Linkify

import java.util.ArrayList
import java.util.HashMap

import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.model.Message
import drift.com.drift.model.MessageRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 11/08/2017.
 */

object MessagesWrapper {

    private val TAG = MessagesWrapper::class.java.simpleName

    fun getMessagesForConversationId(conversationId: Int, callback: (response: ArrayList<Message>?) -> Unit) {

        APIManager.conversationClient.getMessages(conversationId).enqueue(object : Callback<ArrayList<Message>> {
            override fun onResponse(call: Call<ArrayList<Message>>, response: Response<ArrayList<Message>>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<Message>>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

    fun sendMessageToConversation(conversationId: Int, message: MessageRequest, callback: (response: Message?) -> Unit) {

        APIManager.conversationClient.postMessage(conversationId, message).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

    fun createConversation(bodyString: String, welcomeMessage: String?, welcomeUserId: Int?, callback: (response: Message?) -> Unit) {

        val payload = HashMap<String, Any>()

        val editableText = SpannableStringBuilder(bodyString)
        Linkify.addLinks(editableText, Linkify.WEB_URLS)
        val body = Html.toHtml(editableText).trim { it <= ' ' }

        payload["body"] = body

        if (welcomeMessage != null && welcomeUserId != null) {
            val sender = HashMap<String, Int>()
            sender["id"] = welcomeUserId


            val preMessage = HashMap<String, Any>()
            preMessage["body"] = welcomeMessage
            preMessage["sender"] = sender

            val attributes = HashMap<String, Any>()
            attributes["preMessages"] = listOf(preMessage)
            attributes["sentWelcomeMessage"] = true


            payload["attributes"] = attributes
        }

        APIManager.conversationClient.createConversation(payload).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<Message>, throwable: Throwable) {
                LoggerHelper.logMessage(TAG, throwable.localizedMessage)
                callback(null)
            }
        })

    }
}
