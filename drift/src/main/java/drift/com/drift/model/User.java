package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 28/07/2017.
 */

public class User {
    @Expose
    @SerializedName("id")
    public Integer id;
    @Expose
    @SerializedName("orgId")
    public Integer orgId;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("avatarUrl")
    public String avatarUrl;
    @Expose
    @SerializedName("bot")
    public Boolean bot = false;

    public String getUserName() {

        if (name != null && !name.isEmpty()) {
            return name;
        } else {
            return "No Name Set";
        }
    }

}
