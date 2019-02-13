package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.joda.time.DateTimeConstants

/**
 * Created by eoin on 28/07/2017.
 */

internal class OpenHour {

    @Expose
    @SerializedName("opens")
    var opens: String? = null
    @Expose
    @SerializedName("closes")
    var closes: String? = null
    @Expose
    @SerializedName("dayOfWeek")
    var dayOfWeek: String? = null

    enum class Weekday {
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


    fun containsDay(day: Int): Boolean {

        try {

            val weekday = Weekday.valueOf(dayOfWeek!!)

            when (weekday) {
                OpenHour.Weekday.MONDAY -> return day == DateTimeConstants.MONDAY
                OpenHour.Weekday.TUESDAY -> return day == DateTimeConstants.TUESDAY
                OpenHour.Weekday.WEDNESDAY -> return day == DateTimeConstants.WEDNESDAY
                OpenHour.Weekday.THURSDAY -> return day == DateTimeConstants.THURSDAY
                OpenHour.Weekday.FRIDAY -> return day == DateTimeConstants.FRIDAY
                OpenHour.Weekday.SATURDAY -> return day == DateTimeConstants.SATURDAY
                OpenHour.Weekday.SUNDAY -> return day == DateTimeConstants.SUNDAY
                OpenHour.Weekday.WEEKDAYS -> return day != DateTimeConstants.SATURDAY && day != DateTimeConstants.SUNDAY
                OpenHour.Weekday.WEEKENDS -> return day == DateTimeConstants.SATURDAY || day == DateTimeConstants.SUNDAY
                OpenHour.Weekday.EVERYDAY -> return true
                else -> return false
            }


        } catch (t: Throwable) {
            t.printStackTrace()
            return false
        }

    }


}
