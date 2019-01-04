package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 03/08/2017.
 */

class SocketAuth {
    @Expose
    @SerializedName("user_id")
    var userId: String? = null
    @Expose
    @SerializedName("session_token")
    var sessionToken: String? = null

    @Expose
    @SerializedName("org_id")
    var orgId: Int = 0
}
