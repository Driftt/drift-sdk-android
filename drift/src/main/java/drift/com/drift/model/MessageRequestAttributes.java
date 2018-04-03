package drift.com.drift.model;

import com.google.gson.annotations.Expose;
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
    @Expose
    @SerializedName("appointmentInfo")
    MessageRequestAttributesAppointment appointment;
    @Expose
    @SerializedName("scheduleMeetingWith")
    int meetingUserId;
    @Expose
    @SerializedName("scheduleMeetingFlow")
    boolean scheduleMeetingFlow = true;


    MessageRequestAttributes(GoogleMeeting googleMeeting, UserAvailability userAvailability, int meetingUserId, int conversationId, Date timeSlot) {

        this.meetingUserId = meetingUserId;
        this.appointment = new MessageRequestAttributesAppointment(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot);

    }

}
