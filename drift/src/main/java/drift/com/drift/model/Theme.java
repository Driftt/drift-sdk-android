package drift.com.drift.model;

import androidx.annotation.NonNull;

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
    private String welcomeMessage;
    @Expose
    @SerializedName("awayMessage")
    private String awayMessage;
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

    @NonNull
    public String getWelcomeMessage() {
        if (welcomeMessage == null || welcomeMessage.isEmpty()){
            return "How can we help out? We are here for you!";
        }
        return welcomeMessage;
    }

    @NonNull
    public String getAwayMessage() {
        if (awayMessage == null || awayMessage.isEmpty()) {
            return "We’re not currently online right now but if you leave a message, we’ll get back to you as soon as possible!";
        }
        return awayMessage;
    }
}
