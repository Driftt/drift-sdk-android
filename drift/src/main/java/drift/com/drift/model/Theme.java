package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by eoin on 28/07/2017.
 */

public class Theme {

    @SerializedName("backgroundColor")
    public String backgroundColor;
    @SerializedName("foregroundColor")
    public String foregroundColor;
    @SerializedName("textColor")
    public String textColor;
    @SerializedName("activeColor")
    public String activeColor;
    @SerializedName("logoUrl")
    public Object logoUrl;
    @SerializedName("iconUrl")
    public Object iconUrl;
    @SerializedName("userListMode")
    public String userListMode;

    @SerializedName("welcomeMessage")
    public String welcomeMessage;
    @SerializedName("awayMessage")
    public String awayMessage;




    @SerializedName("timezone")
    public String timezone;
    @SerializedName("showWelcomeMessage")
    public Boolean showWelcomeMessage;



    @SerializedName("openHours")
    public List<OpenHour> openHours = null;


}
