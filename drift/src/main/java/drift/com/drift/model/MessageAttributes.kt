package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList

/**
 * Created by eoin on 22/02/2018.
 */

internal class MessageAttributes {

    @Expose
    @SerializedName("offerSchedule")
    var offerSchedule = -1

    @Expose
    @SerializedName("presentSchedule")
    var presentSchedule: Long? = null

    @Expose
    @SerializedName("appointmentInfo")
    var appointmentInfo: AppointmentInfo? = null

    @Expose
    @SerializedName("preMessages")
    var preMessages = ArrayList<PreMessage>()

}
