package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Date

/**
 * Created by eoin on 28/02/2018.
 */

internal class MessageRequestAttributes (googleMeeting: GoogleMeeting, userAvailability: UserAvailability, meetingUserId: Int, conversationId: Int, timeSlot: Date) {

    /*
        "scheduleMeetingFlow": true,
        "scheduleMeetingWith":meetingUserId,
        "appointmentInfo":apointment]

     */

    @Expose
    @SerializedName("scheduleMeetingWith")
    internal var meetingUserId: Int = meetingUserId

    @Expose
    @SerializedName("appointmentInfo")
    internal var appointment: MessageRequestAttributesAppointment = MessageRequestAttributesAppointment(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot)
    @Expose
    @SerializedName("scheduleMeetingFlow")
    internal var scheduleMeetingFlow = true


}
