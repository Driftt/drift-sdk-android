package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 04/08/2017.
 */

public class IdentifyResponse {
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("userId")
    public String userId;
    @Expose
    @SerializedName("orgId")
    public Integer orgId;


}
