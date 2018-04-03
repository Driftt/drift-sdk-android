package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by eoin on 22/02/2018.
 */

public class MessageAttributes {

    @SerializedName("scheduleMeetingWith")
    public Integer scheduleMeetingWith;

    @SerializedName("scheduleMeetingFlow")
    public boolean scheduleMeetingFlow = false;

    @SerializedName("offerSchedule")
    public int offerSchedule = -1;

    @SerializedName("presentSchedule")
    public Integer presentSchedule;

    @SerializedName("appointmentInfo")
    public AppointmentInfo appointmentInfo;

    @SerializedName("preMessages")
    public ArrayList<PreMessage> preMessages = new ArrayList<>();

}
