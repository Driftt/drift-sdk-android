package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 22/02/2018.
 */

public class PreMessage {

    @Expose
    @SerializedName("body")
    public String messageBody;
    @Expose
    @SerializedName("sender")
    public User sender;
}
