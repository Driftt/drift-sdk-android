package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date


/**
 * Created by eoin on 28/02/2018.
 */

internal class GoogleMeeting {

    @Expose
    @SerializedName("id")
    var meetingId: String? = null

    @Expose
    @SerializedName("url")
    var meetingURL: String? = null

}
