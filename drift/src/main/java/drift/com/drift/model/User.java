package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by eoin on 28/07/2017.
 */

public class User {

    @SerializedName("id")
    public Integer id;

    @SerializedName("orgId")
    public Integer orgId;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    @SerializedName("bot")
    public Boolean bot;


}
