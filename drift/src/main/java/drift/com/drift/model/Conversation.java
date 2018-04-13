package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 28/07/2017.
 */

public class Conversation {

    @Expose
    @SerializedName("id")
    public Integer id;

    @Expose
    @SerializedName("displayId")
    public Integer displayId;

    @Expose
    @SerializedName("type")
    public String type = "";

    @Expose
    @SerializedName("status")
    public String status;

    @Expose
    @SerializedName("orgId")
    public Integer orgId;

    @Expose
    @SerializedName("inboxId")
    public Integer inboxId;

    @Expose
    @SerializedName("endUserId")
    public Integer endUserId;

    @Expose
    @SerializedName("subject")
    public Object subject;

    @Expose
    @SerializedName("preview")
    public String preview;

    @Expose
    @SerializedName("user")
    public User user;

    @Expose
    @SerializedName("updatedAt")
    public Date updatedAt;

}
