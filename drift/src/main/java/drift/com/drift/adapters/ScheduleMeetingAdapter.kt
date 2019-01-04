package drift.com.drift.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList
import java.util.Date

import androidx.recyclerview.widget.RecyclerView
import drift.com.drift.R
import drift.com.drift.helpers.DateHelper

/**
 * Created by eoin on 27/02/2018.
 */

internal class ScheduleMeetingAdapter : RecyclerView.Adapter<ScheduleMeetingAdapter.ScheduleMeetingCell>() {

    private var dates: List<Date> = ArrayList()

    private var selectionType = SelectionType.DAY

    enum class SelectionType {
        DAY,
        TIME
    }

    fun getItemAt(position: Int): Date {
        return dates[position]
    }

    fun setupForDates(dates: List<Date>, selectionType: SelectionType) {

        this.selectionType = selectionType
        this.dates = dates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleMeetingCell {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.drift_sdk_schedule_meeting_cell, parent, false)
        return ScheduleMeetingCell(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleMeetingCell, position: Int) {

        val currentDate = dates[position]

        when (selectionType) {
            ScheduleMeetingAdapter.SelectionType.DAY -> holder.titleTextView.text = DateHelper.formatDateForScheduleDay(currentDate)
            ScheduleMeetingAdapter.SelectionType.TIME -> holder.titleTextView.text = DateHelper.formatDateForScheduleTime(currentDate)
        }

    }

    override fun getItemCount(): Int {
        return dates.size
    }

    internal inner class ScheduleMeetingCell(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.drift_sdk_schedule_meeting_cell_title)
    }
}
