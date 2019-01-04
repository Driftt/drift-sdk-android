package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 28/07/2017.
 */

public class EndUser {

    @Expose
    @SerializedName("id")
    public Long id;

    @Expose
    @SerializedName("orgId")
    public Integer orgId;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("email")
    public String email;

    @Expose
    @SerializedName("avatarUrl")
    public String avatarUrl;

    @Expose
    @SerializedName("type")
    public String type;

}
