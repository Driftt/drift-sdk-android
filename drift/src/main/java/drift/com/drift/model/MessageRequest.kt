package drift.com.drift.model

import android.content.Context

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList
import java.util.Date
import java.util.UUID

import drift.com.drift.helpers.TextHelper

/**
 * Created by eoin on 15/08/2017.
 */


internal class MessageRequest {

    @Expose
    @SerializedName("body")
    var body: String
    @Expose
    @SerializedName("type")
    var type = "CHAT"
    @Expose
    @SerializedName("authorId")
    private var authorId: Long? = null
    @Expose
    @SerializedName("attachments")
    private var attachments: MutableList<Int>? = null
    @Expose
    @SerializedName("context")
    private var conversationContext: ConversationContext? = null
    @Expose
    @SerializedName("attributes")
    private var attributes: MessageRequestAttributes? = null


    constructor(googleMeeting: GoogleMeeting, userAvailability: UserAvailability, meetingUserId: Int, conversationId: Int, timeSlot: Date) {
        this.body = ""
        this.type = "CHAT"
        attributes = MessageRequestAttributes(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot)
    }

    constructor(body: String, authorId: Long?, attachmentId: Int?, context: Context) {

        this.body = TextHelper.wrapTextForSending(body)
        this.authorId = authorId

        if (attachmentId != null) {
            this.attachments = ArrayList()
            this.attachments!!.add(attachmentId)
        }

        conversationContext = ConversationContext(context)

    }

    fun messageFromRequest(conversationId: Int?): Message {

        val message = Message()
        message.conversationId = conversationId
        message.body = TextHelper.cleanString(body)
        message.authorId = authorId
        message.uuid = UUID.randomUUID().toString()
        message.createdAt = Date()
        message.sendStatus = Message.SendStatus.SENDING
        return message
    }
}