package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 22/02/2018.
 */

public class AppointmentInfo {

    @Expose
    @SerializedName("availabilitySlot")
    public Date availabilitySlot;

    @Expose
    @SerializedName("endUserTimeZone")
    public String endUserTimeZone;

    @Expose
    @SerializedName("agentTimeZone")
    public String agentTimeZone;

    @Expose
    @SerializedName("agentId")
    public Integer agentId;

    @Expose
    @SerializedName("conversationId")
    public Integer conversationId;

    @Expose
    @SerializedName("slotDuration")
    public Integer slotDuration;

}
