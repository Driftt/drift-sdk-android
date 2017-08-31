package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 04/08/2017.
 */

public class IdentifyResponse {
    @SerializedName("id")
    public String id;
    @SerializedName("userId")
    public String userId;
    @SerializedName("orgId")
    public Integer orgId;


}
