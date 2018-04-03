package drift.com.drift.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import drift.com.drift.R;
import drift.com.drift.helpers.DateHelper;

/**
 * Created by eoin on 27/02/2018.
 */

public class ScheduleMeetingAdapter extends RecyclerView.Adapter<ScheduleMeetingAdapter.ScheduleMeetingCell>{

    List<Date> dates = new ArrayList<Date>();

    SelectionType selectionType = SelectionType.DAY;

    public enum SelectionType {
        DAY,
        TIME
    }

    public Date getItemAt(int position){
            return dates.get(position);
    }

    public void setupForDates(List<Date> dates, SelectionType selectionType) {

        this.selectionType = selectionType;
        this.dates = dates;
        notifyDataSetChanged();
    }

    @Override
    public ScheduleMeetingCell onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.drift_sdk_schedule_meeting_cell, parent, false);
        return new ScheduleMeetingCell(itemView);
    }

    @Override
    public void onBindViewHolder(ScheduleMeetingCell holder, int position) {

        Date currentDate = dates.get(position);

        switch (selectionType) {
            case DAY:
                holder.titleTextView.setText(DateHelper.formatDateForScheduleDay(currentDate));
                break;
            case TIME:
                holder.titleTextView.setText(DateHelper.formatDateForScheduleTime(currentDate));

                break;
        }

    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class ScheduleMeetingCell extends RecyclerView.ViewHolder {

        TextView titleTextView;

        ScheduleMeetingCell(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.drift_sdk_schedule_meeting_cell_title);
        }
    }
}
