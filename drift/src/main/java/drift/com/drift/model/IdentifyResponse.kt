package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 04/08/2017.
 */

internal class IdentifyResponse {
    @Expose
    @SerializedName("id")
    var id: String? = null
    @Expose
    @SerializedName("userId")
    var userId: String? = null
    @Expose
    @SerializedName("orgId")
    var orgId: Int? = null


}
