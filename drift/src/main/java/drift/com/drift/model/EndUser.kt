package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 28/07/2017.
 */

class EndUser {

    @Expose
    @SerializedName("id")
    var id: Long? = null

    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null

    @Expose
    @SerializedName("name")
    var name: String? = null

    @Expose
    @SerializedName("email")
    var email: String? = null

    @Expose
    @SerializedName("avatarUrl")
    var avatarUrl: String? = null

    @Expose
    @SerializedName("type")
    var type: String? = null

}
