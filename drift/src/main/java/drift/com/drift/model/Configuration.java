package drift.com.drift.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

/**
 * Created by eoin on 28/07/2017.
 */

public class Configuration {

    public enum WidgetStatus {ON, AWAY}
    public enum WidgetMode{ MANUAL, AUTO}

    @SerializedName("inboxId")
    public Integer inboxId;

    @SerializedName("refreshRate")
    public Integer refreshRate;



    @SerializedName("inboxEmailAddress")
    public String inboxEmailAddress;

    @SerializedName("authClientId")
    public String authClientId;
    @SerializedName("redirectUri")
    public String redirectUri;
    @SerializedName("organizationName")
    public String organizationName;
    @SerializedName("organizationUrl")
    public Object organizationUrl;
    @SerializedName("theme")
    public Theme theme;
    @SerializedName("chatEnabled")
    public Boolean chatEnabled;
    @SerializedName("leadChatEnabled")
    public Boolean leadChatEnabled;
    @SerializedName("enabled")
    public Boolean enabled;
    @SerializedName("widgetStatus")
    public String widgetStatus;
    @SerializedName("widgetMode")
    public String widgetMode;
    @SerializedName("showBranding")
    public Boolean showBranding;

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
