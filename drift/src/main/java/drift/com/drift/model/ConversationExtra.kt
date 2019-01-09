package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 04/08/2017.
 */

internal class ConversationExtra {

    @Expose
    @SerializedName("conversation")
    var conversation: Conversation? = null

    @Expose
    @SerializedName("unreadMessages")
    var unreadMessages: Int = 0

    @Expose
    @SerializedName("lastMessage")
    var lastMessage: Message? = null

    @Expose
    @SerializedName("lastAgentMessage")
    var lastAgentMessage: Message? = null

}
