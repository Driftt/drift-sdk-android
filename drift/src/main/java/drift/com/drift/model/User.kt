package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 28/07/2017.
 */

internal class User {
    @Expose
    @SerializedName("id")
    var id: Int = 0
    @Expose
    @SerializedName("orgId")
    var orgId: Int = 0
    @Expose
    @SerializedName("name")
    private val name: String? = null
    @Expose
    @SerializedName("avatarUrl")
    var avatarUrl: String? = null
    @Expose
    @SerializedName("bot")
    var bot: Boolean? = false

    val userName: String
        get() = if (name != null && !name.isEmpty()) {
            name
        } else {
            "No Name Set"
        }

}
