package drift.com.drift.model

import android.util.Log

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.util.ArrayList
import java.util.Random
import java.util.TimeZone

/**
 * Created by eoin on 28/07/2017.
 */

class Configuration {

    @Expose
    @SerializedName("inboxId")
    var inboxId: Int? = null

    @Expose
    @SerializedName("refreshRate")
    var refreshRate: Int? = null

    @Expose
    @SerializedName("authClientId")
    var authClientId: String? = null

    @Expose
    @SerializedName("redirectUri")
    var redirectUri: String? = null

    @Expose
    @SerializedName("theme")
    var theme: Theme? = null

    @Expose
    @SerializedName("enabled")
    var enabled: Boolean? = null

    @Expose
    @SerializedName("widgetStatus")
    var widgetStatus: String? = null

    @Expose
    @SerializedName("widgetMode")
    var widgetMode: String? = null

    @Expose
    @SerializedName("showBranding")
    var showBranding: Boolean? = null

    @Expose
    @SerializedName("team")
    var team: ArrayList<User>? = null

    val userForWelcomeMessage: User?
        get() {

            if (theme == null) {
                return null
            }
            var selectedUser: User? = null
            if (theme!!.getUserListMode() == Theme.UserListMode.CUSTOM) {

                for (user in team!!) {
                    if (theme!!.userListIds!!.contains(user.id)) {
                        selectedUser = user
                        break
                    }
                }
            }

            if (selectedUser == null) {
                val randomUser = team!![Random().nextInt(team!!.size)]
                if (randomUser != null) {
                    selectedUser = randomUser
                }
            }

            return selectedUser
        }

    //Parse opens time into timezone, sets hours minutes and seconds of current time in timezone
    //Same logic as above for
    val isOrgCurrentlyOpen: Boolean
        get() {

            when (getWidgetMode()) {
                Configuration.WidgetMode.MANUAL -> return getWidgetStatus() == WidgetStatus.ON
                Configuration.WidgetMode.AUTO -> {

                    try {
                        val timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(theme!!.timezone))

                        val df = DateTimeFormat
                                .forPattern("HH:mm:ss")
                                .withZone(timeZone)

                        for (openHour in theme!!.openHours!!) {
                            var nowInTimezone = DateTime.now(timeZone)

                            val dayOfWeek = nowInTimezone.dayOfWeek
                            if (openHour.containsDay(dayOfWeek)) {
                                val dateTime = df.parseDateTime(openHour.opens!!)
                                nowInTimezone = nowInTimezone.hourOfDay().setCopy(dateTime.hourOfDay)
                                nowInTimezone = nowInTimezone.minuteOfHour().setCopy(dateTime.minuteOfHour)
                                nowInTimezone = nowInTimezone.secondOfMinute().setCopy(dateTime.secondOfMinute)
                                val closedDateTime = df.parseDateTime(openHour.closes!!)
                                var closedNowTimezone = DateTime.now(timeZone)
                                closedNowTimezone = closedNowTimezone.hourOfDay().setCopy(closedDateTime.hourOfDay)
                                closedNowTimezone = closedNowTimezone.minuteOfHour().setCopy(closedDateTime.minuteOfHour)
                                closedNowTimezone = closedNowTimezone.secondOfMinute().setCopy(closedDateTime.secondOfMinute)


                                if (Interval(nowInTimezone, closedNowTimezone).containsNow()) {
                                    Log.d("TIME", "Currently in range")
                                    return true
                                }
                            }
                        }

                    } catch (t: Throwable) {

                        return false
                    }

                    return false
                }
                else -> return false
            }
        }

    enum class WidgetStatus {
        ON, AWAY
    }

    enum class WidgetMode {
        MANUAL, AUTO
    }

    fun getWidgetStatus(): WidgetStatus {
        try {
            return WidgetStatus.valueOf(widgetStatus!!)
        } catch (t: Throwable) {
            t.printStackTrace()
            return WidgetStatus.ON
        }

    }

    fun getWidgetMode(): WidgetMode {

        try {
            return WidgetMode.valueOf(widgetMode!!)
        } catch (t: Throwable) {
            t.printStackTrace()
            return WidgetMode.MANUAL
        }

    }

}
