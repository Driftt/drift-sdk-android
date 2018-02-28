package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 28/02/2018.
 */

public class MessageRequestAttributes {

    /*
        "scheduleMeetingFlow": true,
        "scheduleMeetingWith":meetingUserId,
        "appointmentInfo":apointment]

     */

    @SerializedName("appointmentInfo")
    MessageRequestAttributesAppointment appointment;

    @SerializedName("scheduleMeetingWith")
    int meetingUserId;

    @SerializedName("scheduleMeetingFlow")
    boolean scheduleMeetingFlow = true;


    MessageRequestAttributes(GoogleMeeting googleMeeting, UserAvailability userAvailability, int meetingUserId, int conversationId, Date timeSlot) {

        this.meetingUserId = meetingUserId;
        this.appointment = new MessageRequestAttributesAppointment(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot);

    }

}
