package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by eoin on 22/02/2018.
 */

class PreMessage {

    @Expose
    @SerializedName("body")
    var messageBody: String? = null
    @Expose
    @SerializedName("sender")
    var sender: User? = null
}
