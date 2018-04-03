package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;


import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eoin on 27/02/2018.
 */

public class UserAvailability {

    @SerializedName("slotDuration")
    public int slotDuration = 0;
    @SerializedName("agentTimezone")
    public String agentTimezone;
    @SerializedName("slots")
    public List<Date> slots = null;


    public ArrayList<Date> getUniqueDates(){

        ArrayList<LocalDate> seenDays = new ArrayList<>();

        if (slots != null) {

            for (Date date : slots) {
                LocalDate localDate = new LocalDate(date);
                if (!seenDays.contains(localDate)) {
                    seenDays.add(localDate);
                }
            }
        }

        ArrayList<Date> output = new ArrayList<>();

        for (LocalDate localDate : seenDays) {
            output.add(localDate.toDate());
        }

        return output;
    }

    public ArrayList<Date> getDatesForDay(Date chosenDate) {

        LocalDate localChosenDate = new LocalDate(chosenDate);
        ArrayList<Date> output = new ArrayList<>();

        if (slots != null) {

            for (Date date : slots) {
                LocalDate localDate = new LocalDate(date);

                if (localChosenDate.isEqual(localDate)) {
                    output.add(date);
                }
            }
        }

        return output;
    }
}
