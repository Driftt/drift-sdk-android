package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by eoin on 28/02/2018.
 */

public class MessageRequestAttributesAppointment {


    @SerializedName("id")
    String googleMeetingId;

    @SerializedName("url")
    String googleMeetingURL;

    @SerializedName("availabilitySlot")
    Date dateSlot;

    @SerializedName("slotDuration")
    int slotDuration;

    @SerializedName("agentId")
    int agentId;

    @SerializedName("conversationId")
    int conversationId;


    @SerializedName("endUserTimeZone")
    String endUserTimeZone;

    @SerializedName("agentTimeZone")
    String agentTimeZone;

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

    MessageRequestAttributesAppointment(GoogleMeeting googleMeeting, UserAvailability userAvailability, int meetingUserId, int conversationId, Date timeSlot) {
        googleMeetingId = googleMeeting.meetingId;
        googleMeetingURL = googleMeeting.meetingURL;
        dateSlot = timeSlot;
        slotDuration = userAvailability.slotDuration;
        agentId = meetingUserId;
        this.conversationId = conversationId;
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        endUserTimeZone = tz.getDisplayName();
        agentTimeZone = userAvailability.agentTimezone;
    }

}
