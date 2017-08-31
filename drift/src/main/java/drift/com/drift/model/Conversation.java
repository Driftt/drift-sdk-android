package drift.com.drift.model;

import android.text.format.DateFormat;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by eoin on 28/07/2017.
 */

public class Conversation {

    @SerializedName("id")
    public Integer id;
    @SerializedName("displayId")
    public Integer displayId;
    @SerializedName("type")
    public String type;
    @SerializedName("status")
    public String status;
    @SerializedName("orgId")
    public Integer orgId;
    @SerializedName("inboxId")
    public Integer inboxId;
    @SerializedName("endUserId")
    public Integer endUserId;
    @SerializedName("subject")
    public Object subject;
    @SerializedName("preview")
    public String preview;
    @SerializedName("user")
    public User user;

    @SerializedName("updatedAt")
    public Date updatedAt;

}
