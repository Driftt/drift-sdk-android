package drift.com.drift.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


import org.joda.time.LocalDate

import java.util.ArrayList
import java.util.Date

/**
 * Created by eoin on 27/02/2018.
 */

internal class UserAvailability {

    @Expose
    @SerializedName("slotDuration")
    var slotDuration = 0

    @Expose
    @SerializedName("agentTimezone")
    var agentTimezone: String? = null

    @Expose
    @SerializedName("slots")
    var slots: List<Date>? = null


    val uniqueDates: ArrayList<Date>
        get() {

            val seenDays = ArrayList<LocalDate>()

            if (slots != null) {

                for (date in slots!!) {
                    val localDate = LocalDate(date)
                    if (!seenDays.contains(localDate)) {
                        seenDays.add(localDate)
                    }
                }
            }

            val output = ArrayList<Date>()

            for (localDate in seenDays) {
                output.add(localDate.toDate())
            }

            return output
        }

    fun getDatesForDay(chosenDate: Date): ArrayList<Date> {

        val localChosenDate = LocalDate(chosenDate)
        val output = ArrayList<Date>()

        if (slots != null) {

            for (date in slots!!) {
                val localDate = LocalDate(date)

                if (localChosenDate.isEqual(localDate)) {
                    output.add(date)
                }
            }
        }

        return output
    }
}
