package drift.com.drift.helpers

import android.content.Context
import android.text.format.DateFormat

import java.text.SimpleDateFormat
import java.util.Date

import drift.com.drift.Drift


internal object DateHelper {
    fun isSameDay(dateOne: Date?, dateTwo: Date?): Boolean {

        if (dateOne == null || dateTwo == null) {
            return false
        }

        val fmt = SimpleDateFormat("yyyyMMdd")

        return fmt.format(dateOne) == fmt.format(dateTwo)
    }

    fun formatDateForConversation(context: Context, date: Date?): String {

        if (date == null){
            return ""
        }

        return if (isSameDay(date, Date())) {
            DateFormat.getTimeFormat(context).format(date)
        } else {
            DateFormat.getDateFormat(context).format(date)
        }
    }

    fun formatDateForScheduleDay(context: Context, date: Date): String {
        return DateFormat.getDateFormat(context).format(date)
    }

    fun formatDateForScheduleTime(context: Context, date: Date): String {
        return DateFormat.getTimeFormat(context).format(date)
    }
}