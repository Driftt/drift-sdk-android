package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 28/07/2017.
 */

public class EndUser {

    @SerializedName("id")
    public Integer id;

    @SerializedName("orgId")
    public Integer orgId;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("timeZone")
    public String timeZone;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    @SerializedName("externalId")
    public String externalId;

    @SerializedName("type")
    public String type;

}
