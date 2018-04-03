package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 28/07/2017.
 */

public class User {

    @SerializedName("id")
    public Integer id;

    @SerializedName("orgId")
    public Integer orgId;

    @SerializedName("name")
    private String name;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    @SerializedName("bot")
    public Boolean bot;

    public String getUserName() {

        if (name != null && !name.isEmpty()) {
            return name;
        } else {
            return "No Name Set";
        }
    }

}
