package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 22/02/2018.
 */

public class AppointmentInfo {

    @SerializedName("availabilitySlot")
    public Date availabilitySlot;

    @SerializedName("endUserTimeZone")
    public String endUserTimeZone;

    @SerializedName("agentTimeZone")
    public String agentTimeZone;

    @SerializedName("agentId")
    public Integer agentId;

    @SerializedName("conversationId")
    public Integer conversationId;

    @SerializedName("slotDuration")
    public Integer slotDuration;

}
