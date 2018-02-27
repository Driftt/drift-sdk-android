package drift.com.drift.helpers;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import drift.com.drift.Drift;



public class DateHelper {
    public static boolean isSameDay(Date dateOne, Date dateTwo) {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        if (fmt.format(dateOne).equals(fmt.format(dateTwo))) {
            return true;
        }
        return false;
    }

    public static String formatDateForConversation(Date date){

        if (isSameDay(date, new Date())){
            return DateFormat.getTimeFormat(Drift.getContext()).format(date);
        }else{
            return DateFormat.getDateFormat(Drift.getContext()).format(date);
        }
    }

    public static String formatDateForScheduleDay(Date date){
        return DateFormat.getDateFormat(Drift.getContext()).format(date);
    }

    public static String formatDateForScheduleTime(Date date){
        return DateFormat.getTimeFormat(Drift.getContext()).format(date);
    }
}