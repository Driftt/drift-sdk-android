package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList
import java.util.Date

import drift.com.drift.helpers.TextHelper

/**
 * Created by eoin on 28/07/2017.
 */

internal class Message {

    @Expose
    @SerializedName("id")
    var id: Long? = null
    @Expose
    @SerializedName("type")
    var type: String? = null
    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null
    @Expose
    @SerializedName("conversationId")
    var conversationId: Int? = null
    @Expose
    @SerializedName("authorType")
    var authorType: String? = null
    @Expose
    @SerializedName("body")
    var body: String? = null
    @Expose
    @SerializedName("uuid")
    var uuid: String? = null
    @Expose
    @SerializedName("contentType")
    var contentType: String? = null
    @Expose
    @SerializedName("authorId")
    var authorId: Long = 0L
    @Expose
    @SerializedName("createdAt")
    var createdAt: Date? = null
    @Expose
    @SerializedName("attachments")
    var attachmentIds: ArrayList<Int>? = null
    @Expose
    @SerializedName("attributes")
    var attributes: MessageAttributes? = null

    private var formattedString: String? = null

    var preMessage = false

    var fakeMessage = false

    var sendStatus = SendStatus.SENT

    val isMessageFromEndUser: Boolean?
        get() {

            val auth = Auth.instance
            return if (auth?.endUser != null) {
                auth.endUser?.id == authorId
            } else false
        }

    enum class SendStatus {
        SENDING,
        SENT,
        FAILED
    }

    fun getFormattedString(): String? {
        if (formattedString == null) {
            formattedString = TextHelper.cleanString(body ?: "")
        }

        return formattedString
    }

    fun readableSendStatus(): String {
        return when (sendStatus) {
            Message.SendStatus.SENDING -> "Sending..."
            Message.SendStatus.FAILED -> "Failed to send"
            Message.SendStatus.SENT -> ""
        }
    }

}
