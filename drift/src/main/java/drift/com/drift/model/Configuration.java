package drift.com.drift.model;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by eoin on 28/07/2017.
 */

public class Configuration {

    public enum WidgetStatus {ON, AWAY}
    public enum WidgetMode{ MANUAL, AUTO}

    @Expose
    @SerializedName("inboxId")
    public Integer inboxId;

    @Expose
    @SerializedName("refreshRate")
    public Integer refreshRate;

    @Expose
    @SerializedName("authClientId")
    public String authClientId;

    @Expose
    @SerializedName("redirectUri")
    public String redirectUri;

    @Expose
    @SerializedName("theme")
    public Theme theme;

    @Expose
    @SerializedName("enabled")
    public Boolean enabled;

    @Expose
    @SerializedName("widgetStatus")
    public String widgetStatus;

    @Expose
    @SerializedName("widgetMode")
    public String widgetMode;

    @Expose
    @SerializedName("showBranding")
    public Boolean showBranding;

    @Expose
    @SerializedName("team")
    public ArrayList<User> team;

    public WidgetStatus getWidgetStatus() {
        try {
            return WidgetStatus.valueOf(widgetStatus);
        }catch (Throwable t) {
            t.printStackTrace();
            return WidgetStatus.ON;
        }
    }

    public WidgetMode getWidgetMode() {

        try {
            return WidgetMode.valueOf(widgetMode);
        }catch (Throwable t) {
            t.printStackTrace();
            return WidgetMode.MANUAL;
        }
    }

    @Nullable
    public User getUserForWelcomeMessage(){

        if (theme == null) {
            return null;
        }
        User selectedUser = null;
        if (theme.getUserListMode() == Theme.UserListMode.CUSTOM) {

            for (User user : team) {
                if (theme.userListIds.contains(user.id)) {
                    selectedUser = user;
                    break;
                }
            }
        }

        if (selectedUser == null) {
            User randomUser = team.get((new Random()).nextInt(team.size()));
            if (randomUser != null) {
                selectedUser = randomUser;
            }
        }

        return selectedUser;
    }

    public boolean isOrgCurrentlyOpen(){

        switch (getWidgetMode()) {
            case MANUAL:
                return getWidgetStatus() == WidgetStatus.ON;
            case AUTO:

                try {
                    DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(theme.timezone));

                    final DateTimeFormatter df = DateTimeFormat
                            .forPattern("HH:mm:ss")
                            .withZone(timeZone);

                    for (OpenHour openHour : theme.openHours) {
                        DateTime nowInTimezone = DateTime.now(timeZone);

                        int dayOfWeek = nowInTimezone.getDayOfWeek();
                        if (openHour.containsDay(dayOfWeek)) {

                            //Parse opens time into timezone, sets hours minutes and seconds of current time in timezone
                            DateTime dateTime = df.parseDateTime(openHour.opens);
                            nowInTimezone = nowInTimezone.hourOfDay().setCopy(dateTime.getHourOfDay());
                            nowInTimezone = nowInTimezone.minuteOfHour().setCopy(dateTime.getMinuteOfHour());
                            nowInTimezone = nowInTimezone.secondOfMinute().setCopy(dateTime.getSecondOfMinute());


                            //Same logic as above for
                            DateTime closedDateTime = df.parseDateTime(openHour.closes);
                            DateTime closedNowTimezone = DateTime.now(timeZone);
                            closedNowTimezone = closedNowTimezone.hourOfDay().setCopy(closedDateTime.getHourOfDay());
                            closedNowTimezone = closedNowTimezone.minuteOfHour().setCopy(closedDateTime.getMinuteOfHour());
                            closedNowTimezone = closedNowTimezone.secondOfMinute().setCopy(closedDateTime.getSecondOfMinute());


                            if (new Interval(nowInTimezone, closedNowTimezone).containsNow()) {
                                Log.d("TIME", "Currently in range");
                                return true;
                            }
                        }
                    }

                } catch (Throwable t){

                    return false;
                }

                return false;
            default:
                return false;
        }
    }

}
