package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


/**
 * Created by eoin on 28/02/2018.
 */

public class GoogleMeeting {

    @Expose
    @SerializedName("id")
    public String meetingId;

    @Expose
    @SerializedName("url")
    public String meetingURL;

    @Expose
    @SerializedName("start")
    public Date startTime;

    @Expose
    @SerializedName("end")
    public Date endTime;

}
