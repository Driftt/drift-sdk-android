package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date

/**
 * Created by eoin on 28/07/2017.
 */

internal class Conversation {

    @Expose
    @SerializedName("id")
    var id: Int? = null

    @Expose
    @SerializedName("displayId")
    var displayId: Int? = null

    @Expose
    @SerializedName("type")
    var type = ""

    @Expose
    @SerializedName("status")
    var status: String? = null

    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null

    @Expose
    @SerializedName("inboxId")
    var inboxId: Int? = null

    @Expose
    @SerializedName("endUserId")
    var endUserId: Int? = null

    @Expose
    @SerializedName("subject")
    var subject: Any? = null

    @Expose
    @SerializedName("preview")
    var preview: String? = null

    @Expose
    @SerializedName("user")
    var user: User? = null

    @Expose
    @SerializedName("updatedAt")
    var updatedAt: Date? = null

}
