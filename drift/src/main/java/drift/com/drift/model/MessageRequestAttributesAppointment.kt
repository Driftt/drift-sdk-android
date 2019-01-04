package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Created by eoin on 28/02/2018.
 */

class MessageRequestAttributesAppointment
/*

        "id":googleMeetingId,
        "url": googleMeetingURL,
        "availabilitySlot": meetingTimeSlot.timeIntervalSince1970*1000,
        "slotDuration": meetingDuration,
        "agentId": meetingUserId,
        "conversationId": conversationId,
        "endUserTimeZone": TimeZone.current.identifier,
        "agentTimeZone": agentTimeZone


     */
internal constructor(googleMeeting: GoogleMeeting, userAvailability: UserAvailability,
agentId: Int, conversationId: Int, dateSlot: Date) {

    @Expose
    @SerializedName("agentId")
    internal var agentId: Int = agentId

    @Expose
    @SerializedName("conversationId")
    internal var conversationId: Int = conversationId

    @Expose
    @SerializedName("availabilitySlot")
    internal var dateSlot: Date = dateSlot

    @Expose
    @SerializedName("id")
    internal var googleMeetingId: String? = null
    @Expose
    @SerializedName("url")
    internal var googleMeetingURL: String? = null
    @Expose
    @SerializedName("slotDuration")
    internal var slotDuration: Int = 0

    @Expose
    @SerializedName("endUserTimeZone")
    internal var endUserTimeZone: String
    @Expose
    @SerializedName("agentTimeZone")
    internal var agentTimeZone: String? = null

    init {
        googleMeetingId = googleMeeting.meetingId
        googleMeetingURL = googleMeeting.meetingURL
        slotDuration = userAvailability.slotDuration
        val cal = Calendar.getInstance()
        val tz = cal.timeZone

        endUserTimeZone = tz.id
        agentTimeZone = userAvailability.agentTimezone
    }

}
