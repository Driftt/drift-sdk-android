package drift.com.drift.managers

import java.util.ArrayList
import java.util.Comparator
import java.util.Date
import java.util.HashMap
import java.util.UUID

import drift.com.drift.helpers.TextHelper
import drift.com.drift.model.Message
import drift.com.drift.model.MessageRequest
import drift.com.drift.model.PreMessage
import drift.com.drift.wrappers.MessagesWrapper

/**
 * Created by eoin on 11/08/2017.
 */

internal class MessageManager {

    private var messageCache = HashMap<Int, ArrayList<Message>>()
    private var failedMessageCache = HashMap<Int, ArrayList<Message>>()

    fun clearCache() {
        messageCache = HashMap()
        failedMessageCache = HashMap()
    }

    fun addMessageToConversation(conversationId: Int, message: Message): ArrayList<Message> {

        val successMessages = messageCache[conversationId]

        if (successMessages != null) {
            successMessages.add(0, message)
        } else {
            val newMessages = ArrayList<Message>()
            newMessages.add(message)
            messageCache[conversationId] = newMessages
        }

        messageCache[conversationId] = sortMessagesForConversations(messageCache[conversationId])

        return getMessagesForConversationId(conversationId)!!

    }


    fun getMessagesForConversationId(conversationId: Int): ArrayList<Message>? {

        val failedMessages = failedMessageCache[conversationId]

        if (failedMessages == null || failedMessages.isEmpty()) {
            return messageCache[conversationId]
        }

        var successMessages = messageCache[conversationId]

        if (successMessages == null || successMessages.isEmpty()) {
            successMessages = ArrayList()
        }

        val returnedArr = ArrayList<Message>()

        returnedArr.addAll(successMessages)
        returnedArr.addAll(failedMessages)


        returnedArr.sortWith(Comparator { o1, o2 -> if (o1.createdAt == null || o2.createdAt == null) 0 else o2.createdAt!!.compareTo(o1.createdAt) })

        return returnedArr
    }

    private fun getMessagesFromPreMessages(message: Message, preMessages: ArrayList<PreMessage>): ArrayList<Message> {

        val fakeMessages = ArrayList<Message>()

        for (i in preMessages.indices) {

            val preMessage = preMessages[i]

            val fakeMessage = Message()

            fakeMessage.createdAt = Date(message.createdAt!!.time - (i + 1) * 100000)
            fakeMessage.conversationId = message.conversationId
            fakeMessage.body = TextHelper.cleanString(preMessage.messageBody ?: "")

            fakeMessage.fakeMessage = true
            fakeMessage.preMessage = true

            fakeMessage.uuid = UUID.randomUUID().toString()

            fakeMessage.sendStatus = Message.SendStatus.SENT
            fakeMessage.contentType = "CHAT"
            fakeMessage.authorType = "USER"

            if (preMessage.sender != null) {
                fakeMessage.authorId = preMessage.sender!!.id!!.toLong()
                fakeMessages.add(fakeMessage)
            }
        }


        return fakeMessages
    }

    private fun sortMessagesForConversations(rawMessages: ArrayList<Message>?): ArrayList<Message> {
        val output = ArrayList<Message>()

        val sorted = ArrayList(rawMessages!!)


        sorted.sortWith(Comparator { o1, o2 -> if (o1.createdAt == null || o2.createdAt == null) 0 else o1.createdAt!!.compareTo(o2.createdAt) })

        for (message in sorted) {


            if (message.preMessage) {
                continue
            }

            if (message.attributes != null && !message.attributes!!.preMessages.isEmpty()) {
                output.addAll(getMessagesFromPreMessages(message, message.attributes!!.preMessages))
            }

            if (message.attributes != null && message.attributes!!.offerSchedule != -1) {
                continue
            }

            if (message.attributes != null && message.attributes!!.appointmentInfo != null) {


                for (message1 in output) {

                    if (message1.attributes != null && message1.attributes!!.presentSchedule != null) {
                        message1.attributes!!.presentSchedule = null
                        break
                    }

                }

            }

            output.add(message)

        }

        output.sortWith(Comparator { o1, o2 -> if (o1.createdAt == null || o2.createdAt == null) 0 else o2.createdAt!!.compareTo(o1.createdAt) })


        return output
    }

    fun addMessageFailedToConversation(message: Message, conversationId: Int) {

        val successMessages = messageCache[conversationId]

        if (successMessages != null && successMessages.contains(message)) {
            successMessages.remove(message)
        }

        var failedMessages = failedMessageCache[conversationId]

        if (failedMessages != null && !failedMessages.contains(message)) {
            failedMessages.add(message)
            return
        }

        failedMessages = ArrayList()
        failedMessages.add(message)

        failedMessageCache[conversationId] = failedMessages
    }


    fun removeMessageFromFailedCache(message: Message, conversationId: Int) {

        val failedMessages = failedMessageCache[conversationId]

        if (failedMessages != null && failedMessages.contains(message)) {
            failedMessages.remove(message)
        }
    }

    fun getMessagesForConversation(conversationId: Int, conversationsCallback: (response: ArrayList<Message>?) -> Unit) {

        MessagesWrapper.getMessagesForConversationId(conversationId) { response ->
            if (response != null) {
                response.reverse()
                val conversationMessages = sortMessagesForConversations(response)

                messageCache[conversationId] = conversationMessages
            }

            conversationsCallback(getMessagesForConversationId(conversationId))
        }
    }

    fun sendMessageForConversationId(conversationId: Int, messageRequest: MessageRequest, conversationsCallback: (response: Message?) -> Unit) {


        MessagesWrapper.sendMessageToConversation(conversationId, messageRequest) { response -> conversationsCallback(response) }

    }

    fun createConversation(body: String, welcomeMessage: String?, welcomeUserId: Int?, callbackWrapper: (response: Message?) -> Unit) {

        MessagesWrapper.createConversation(body, welcomeMessage, welcomeUserId) { response -> callbackWrapper(response) }

    }

    companion object {

        val instance = MessageManager()
    }
}
