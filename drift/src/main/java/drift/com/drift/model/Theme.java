package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eoin on 28/07/2017.
 */

public class Theme {

    public enum UserListMode{ RANDOM, CUSTOM}

    @Expose
    @SerializedName("backgroundColor")
    public String backgroundColor;
    @Expose
    @SerializedName("foregroundColor")
    public String foregroundColor;
    @Expose
    @SerializedName("textColor")
    public String textColor;
    @Expose
    @SerializedName("welcomeMessage")
    public String welcomeMessage;
    @Expose
    @SerializedName("awayMessage")
    public String awayMessage;
    @Expose
    @SerializedName("userListMode")
    public String userListMode;
    @Expose
    @SerializedName("userList")
    public ArrayList<Integer> userListIds;
    @Expose
    @SerializedName("timezone")
    public String timezone;
    @Expose
    @SerializedName("showWelcomeMessage")
    public Boolean showWelcomeMessage;
    @Expose
    @SerializedName("openHours")
    public List<OpenHour> openHours = null;


    public UserListMode getUserListMode(){
        try {
            return UserListMode.valueOf(userListMode);
        }catch (Throwable t) {
            t.printStackTrace();
            return UserListMode.RANDOM;
        }
    }

}
