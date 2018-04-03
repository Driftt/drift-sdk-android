package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTimeConstants;

/**
 * Created by eoin on 28/07/2017.
 */

public class OpenHour {

    public enum Weekday {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY,
        EVERYDAY,
        WEEKDAYS,
        WEEKENDS
    }


    @SerializedName("opens")
    public String opens;

    @SerializedName("closes")
    public String closes;

    @SerializedName("dayOfWeek")
    public String dayOfWeek;


    public boolean containsDay(int day) {

        try {

            Weekday weekday = Weekday.valueOf(dayOfWeek);

            switch (weekday) {
                case MONDAY:
                    return day == DateTimeConstants.MONDAY;
                case TUESDAY:
                    return day == DateTimeConstants.TUESDAY;
                case WEDNESDAY:
                    return day == DateTimeConstants.WEDNESDAY;
                case THURSDAY:
                    return day == DateTimeConstants.THURSDAY;
                case FRIDAY:
                    return day == DateTimeConstants.FRIDAY;
                case SATURDAY:
                    return day == DateTimeConstants.SATURDAY;
                case SUNDAY:
                    return day == DateTimeConstants.SUNDAY;
                case WEEKDAYS:
                    return (day != DateTimeConstants.SATURDAY && day != DateTimeConstants.SUNDAY);
                case WEEKENDS:
                    return (day == DateTimeConstants.SATURDAY || day == DateTimeConstants.SUNDAY);
                case EVERYDAY:
                    return true;
                default:
                    return false;
            }


        }catch (Throwable t){
            t.printStackTrace();
            return false;
        }

    }


}
