package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 03/08/2017.
 */

public class SocketAuth {
    @Expose
    @SerializedName("user_id")
    public String userId;
    @Expose
    @SerializedName("session_token")
    public String sessionToken;

    @Expose
    @SerializedName("org_id")
    public int orgId;
}
