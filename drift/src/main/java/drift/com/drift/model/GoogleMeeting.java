package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


/**
 * Created by eoin on 28/02/2018.
 */

public class GoogleMeeting {

    @SerializedName("id")
    public String meetingId;

    @SerializedName("url")
    public String meetingURL;

    @SerializedName("start")
    public Date startTime;

    @SerializedName("end")
    public Date endTime;

}
