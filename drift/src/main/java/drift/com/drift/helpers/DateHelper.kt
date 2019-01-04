package drift.com.drift.helpers

import android.text.format.DateFormat

import java.text.SimpleDateFormat
import java.util.Date

import drift.com.drift.Drift


object DateHelper {
    fun isSameDay(dateOne: Date?, dateTwo: Date?): Boolean {

        if (dateOne == null || dateTwo == null) {
            return false
        }

        val fmt = SimpleDateFormat("yyyyMMdd")

        return fmt.format(dateOne) == fmt.format(dateTwo)
    }

    fun formatDateForConversation(date: Date?): String {

        if (date == null){
            return ""
        }

        return if (isSameDay(date, Date())) {
            DateFormat.getTimeFormat(Drift.getContext()).format(date)
        } else {
            DateFormat.getDateFormat(Drift.getContext()).format(date)
        }
    }

    fun formatDateForScheduleDay(date: Date): String {
        return DateFormat.getDateFormat(Drift.getContext()).format(date)
    }

    fun formatDateForScheduleTime(date: Date): String {
        return DateFormat.getTimeFormat(Drift.getContext()).format(date)
    }
}