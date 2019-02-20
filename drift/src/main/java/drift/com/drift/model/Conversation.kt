package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date

/**
 * Created by eoin on 28/07/2017.
 */

internal class Conversation {

    enum class ConversationStatus {
        OPEN, CLOSED, PENDING;
    }

    @Expose
    @SerializedName("id")
    var id: Long? = null

    @Expose
    @SerializedName("type")
    var type = ""

    @Expose
    @SerializedName("status")
    private var status: String? = null

    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null

    @Expose
    @SerializedName("preview")
    var preview: String? = null

    @Expose
    @SerializedName("updatedAt")
    var updatedAt: Date? = null

    val conversationStatus: ConversationStatus?
        get() {
            return try {
                status?.let { ConversationStatus.valueOf(it) }
            } catch (t: Throwable) {
                null
            }

        }

}
