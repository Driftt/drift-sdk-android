package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by eoin on 27/02/2018.
 */

public class UserAvailability {

    @SerializedName("slotDuration")
    public Integer slotDuration;
    @SerializedName("agentTimezone")
    public String agentTimezone;
    @SerializedName("slots")
    public List<Date> slots = null;

}
