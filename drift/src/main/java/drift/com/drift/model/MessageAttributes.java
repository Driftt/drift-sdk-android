package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by eoin on 22/02/2018.
 */

public class MessageAttributes {

    @Expose
    @SerializedName("scheduleMeetingWith")
    public Integer scheduleMeetingWith;

    @Expose
    @SerializedName("scheduleMeetingFlow")
    public boolean scheduleMeetingFlow = false;

    @Expose
    @SerializedName("offerSchedule")
    public int offerSchedule = -1;

    @Expose
    @SerializedName("presentSchedule")
    public Integer presentSchedule;

    @Expose
    @SerializedName("appointmentInfo")
    public AppointmentInfo appointmentInfo;

    @Expose
    @SerializedName("preMessages")
    public ArrayList<PreMessage> preMessages = new ArrayList<>();

}
