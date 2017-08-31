package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 03/08/2017.
 */

public class SocketAuth {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("session_token")
    public String sessionToken;
}
