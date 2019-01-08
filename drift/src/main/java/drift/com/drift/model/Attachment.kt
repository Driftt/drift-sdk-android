package drift.com.drift.model

import android.net.Uri

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date

/**
 * Created by eoin on 25/08/2017.
 */

class Attachment {

    @Expose
    @SerializedName("id")
    var id: Int = 0

    @Expose
    @SerializedName("conversationId")
    var conversationId: Int? = null

    @Expose
    @SerializedName("fileName")
    var fileName: String? = null

    @Expose
    @SerializedName("mimeType")
    var mimeType: String? = null

    @Expose
    @SerializedName("size")
    var size: Int? = null

    @Expose
    @SerializedName("createdAt")
    var createdAt: Date? = null

    val isImage: Boolean
        get() = mimeType!!.equals("image/jpeg", ignoreCase = true) || mimeType!!.equals("image/png", ignoreCase = true) || mimeType!!.equals("image/gif", ignoreCase = true) || mimeType!!.equals("image/jpg", ignoreCase = true)

    val url: String
        get() = "https://conversation.api.driftt.com/attachments/$id/data"
}
