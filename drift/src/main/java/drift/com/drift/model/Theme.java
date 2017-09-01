package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eoin on 28/07/2017.
 */

public class Theme {

    public enum UserListMode{ RANDOM, CUSTOM}


    @SerializedName("backgroundColor")
    public String backgroundColor;
    @SerializedName("foregroundColor")
    public String foregroundColor;
    @SerializedName("textColor")
    public String textColor;


    @SerializedName("welcomeMessage")
    public String welcomeMessage;
    @SerializedName("awayMessage")
    public String awayMessage;


    @SerializedName("userListMode")
    public String userListMode;

    @SerializedName("userList")
    public ArrayList<Integer> userListIds;



    @SerializedName("timezone")
    public String timezone;
    @SerializedName("showWelcomeMessage")
    public Boolean showWelcomeMessage;



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
