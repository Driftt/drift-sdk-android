package drift.com.drift.fragments

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView


import java.util.Calendar
import java.util.Date
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import drift.com.drift.R
import drift.com.drift.adapters.ScheduleMeetingAdapter
import drift.com.drift.helpers.Alert
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.DateHelper
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.RecyclerTouchListener
import drift.com.drift.managers.MessageManager
import drift.com.drift.managers.UserManager
import drift.com.drift.model.GoogleMeeting
import drift.com.drift.model.MessageRequest
import drift.com.drift.model.UserAvailability
import drift.com.drift.wrappers.ScheduleMeetingWrapper


internal class ScheduleMeetingDialogFragment : DialogFragment() {

    private var userId: Int = 0
    private var conversationId: Int = 0

    private var scheduleMeetingState = ScheduleMeetingState.DAY

    private var selectedDate: Date? = null

    private var selectedTime: Date? = null

    private var userAvailability: UserAvailability? = null

    private lateinit var headerTitleTextView: TextView
    private lateinit var headerDurationTextView: TextView
    private lateinit var headerRelativeLayout: RelativeLayout

    private lateinit var userTextView: TextView
    private lateinit var userImageView: ImageView

    private lateinit var titleTextView: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerView: RecyclerView

    private lateinit var confirmationScrollView: ScrollView

    private lateinit var confirmationTimeTextView: TextView
    private lateinit var confirmationDateTextView: TextView
    private lateinit var confirmationTimezoneTextView: TextView
    private lateinit var confirmationButton: Button

    private lateinit var backChevron: ImageButton

    private lateinit var adapter: ScheduleMeetingAdapter


    internal enum class ScheduleMeetingState {
        DAY,
        TIME,
        CONFIRM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            userId = arguments!!.getInt(USER_ID_ARG)
            conversationId = arguments!!.getInt(CONVERSATION_ID_ARG)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_schedule_meeting_dialog, container, false)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        headerTitleTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_header_title_text_view)
        headerDurationTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_duration_text_view)
        headerRelativeLayout = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_header_relative_layout)

        userTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_user_text_view)
        userImageView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_user_image_view)

        titleTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_title_text_view)

        recyclerView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_recycler_view)

        progressBar = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_progress_bar)

        confirmationScrollView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_scroll_view)

        confirmationTimeTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_time_text_view)
        confirmationDateTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_date_text_view)
        confirmationTimezoneTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_timezone_text_view)

        confirmationButton = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_button)

        backChevron = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_back_chevron)

        confirmationScrollView.visibility = View.GONE
        headerDurationTextView.text = ""

        headerRelativeLayout.setBackgroundColor(ColorHelper.backgroundColor)
        headerTitleTextView.setTextColor(ColorHelper.foregroundColor)
        headerDurationTextView.setTextColor(ColorHelper.foregroundColor)
        backChevron.setColorFilter(ColorHelper.backgroundColor)


        adapter = ScheduleMeetingAdapter(requireContext())
        recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.drift_sdk_recycler_view_divider)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(requireContext(), recyclerView) { _, position ->
            val chosenDate = adapter.getItemAt(position)
            when (scheduleMeetingState) {
                ScheduleMeetingDialogFragment.ScheduleMeetingState.DAY -> {
                    selectedDate = chosenDate
                    changeToState(ScheduleMeetingState.TIME)
                }
                ScheduleMeetingDialogFragment.ScheduleMeetingState.TIME -> {
                    selectedTime = chosenDate
                    changeToState(ScheduleMeetingState.CONFIRM)
                }

                ScheduleMeetingDialogFragment.ScheduleMeetingState.CONFIRM//Can't hapen
                -> {
                }
            }
        })

        backChevron.setOnClickListener { didPressBackChevron() }

        confirmationButton.setOnClickListener { didPressSchedule() }

        val user = UserManager.instance.getUserForId(userId)
        if (user != null) {
            userTextView.text = user.userName
            val requestOptions = RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.drift_sdk_placeholder)

            Glide.with(activity!!)
                    .load(user.avatarUrl)
                    .apply(requestOptions)
                    .into(userImageView)
        } else {
            userTextView.text = ""
            Glide.with(activity!!).clear(userImageView)
        }

        setupAvailabilityCall()
        changeToState(ScheduleMeetingState.DAY)
        return view
    }

    private fun setupAvailabilityCall() {

        progressBar.visibility = View.VISIBLE
        //TODO: Cancel call on back
        ScheduleMeetingWrapper.getUserAvailability(userId) { response ->
            progressBar.visibility = View.GONE

            if (response != null) {
                userAvailability = response
                headerDurationTextView.text = resources.getString(R.string.drift_sdk_schedule_meeting_minutes, response.slotDuration)
                changeToState(ScheduleMeetingState.DAY)
            } else {
                showAlertForFailedToGetAvailability()
            }
        }
    }

    private fun setupForSelectedDate(date: Date) {


        recyclerView.visibility = View.GONE
        confirmationScrollView.visibility = View.VISIBLE


        confirmationDateTextView.text = DateHelper.formatDateForScheduleDay(requireContext(), date)
        if (userAvailability != null) {
            val cal = Calendar.getInstance()
            val tz = cal.timeZone

            confirmationTimezoneTextView.text = tz.id
            val oneMinuteInMillis: Long = 60000
            val endDate = Date(date.time + userAvailability!!.slotDuration * oneMinuteInMillis)

            confirmationTimeTextView.text = getString(R.string.drift_sdk_dash_divided_strings, DateHelper.formatDateForScheduleTime(requireContext(), date), DateHelper.formatDateForScheduleTime(requireContext(), endDate))

        } else {
            confirmationTimezoneTextView.text = ""
            confirmationTimeTextView.text = ""
        }

    }

    private fun changeToState(state: ScheduleMeetingState) {

        scheduleMeetingState = state

        when (state) {

            ScheduleMeetingDialogFragment.ScheduleMeetingState.CONFIRM -> {
                titleTextView.text = ""
                backChevron.visibility = View.VISIBLE
                if (selectedTime != null) {
                    setupForSelectedDate(selectedTime!!)
                }
            }
            ScheduleMeetingDialogFragment.ScheduleMeetingState.DAY -> {
                titleTextView.setText(R.string.drift_sdk_select_a_day)
                backChevron.visibility = View.INVISIBLE
                if (userAvailability != null) {
                    adapter.setupForDates(userAvailability!!.uniqueDates, ScheduleMeetingAdapter.SelectionType.DAY)
                }
                recyclerView.visibility = View.VISIBLE
                confirmationScrollView.visibility = View.GONE
            }
            ScheduleMeetingDialogFragment.ScheduleMeetingState.TIME -> {
                titleTextView.setText(R.string.drift_sdk_select_a_time)
                backChevron.visibility = View.VISIBLE
                if (userAvailability != null && selectedDate != null) {
                    adapter.setupForDates(userAvailability!!.getDatesForDay(selectedDate!!), ScheduleMeetingAdapter.SelectionType.TIME)
                }
                recyclerView.visibility = View.VISIBLE
                confirmationScrollView.visibility = View.GONE
            }
        }
    }

    private fun didPressBackChevron() {

        when (scheduleMeetingState) {
            ScheduleMeetingDialogFragment.ScheduleMeetingState.CONFIRM -> changeToState(ScheduleMeetingState.TIME)
            ScheduleMeetingDialogFragment.ScheduleMeetingState.TIME -> changeToState(ScheduleMeetingState.DAY)
            ScheduleMeetingDialogFragment.ScheduleMeetingState.DAY -> {
            }
        }
    }

    private fun didPressSchedule() {

        progressBar.visibility = View.VISIBLE

        ScheduleMeetingWrapper.scheduleMeeting(userId, conversationId, selectedTime!!.time.toDouble()) { response ->
            if (response != null) {
                LoggerHelper.logMessage(TAG, response.toString())
                createMessageForMeeting(response)
            } else {
                progressBar.visibility = View.GONE
                showAlertForScheduleMeeting()
            }
        }
    }

    private fun createMessageForMeeting(googleMeeting: GoogleMeeting) {
        progressBar.visibility = View.VISIBLE

        val currentUserAvailability = userAvailability ?: return
        val currentSelectedTime = selectedTime ?: return

        val messageRequest = MessageRequest(googleMeeting, currentUserAvailability, userId, conversationId, currentSelectedTime)

        MessageManager.instance.sendMessageForConversationId(conversationId, messageRequest) { response ->
            progressBar.visibility = View.GONE

            if (response != null) {
                dismiss()
            } else {
                showAlertForCreatingMeetingMessage(googleMeeting)
            }
        }
    }

    private fun showAlertForFailedToGetAvailability() {
        Alert.showAlert(activity, "Error", "Failed to load users availability", "Retry") { setupAvailabilityCall() }
    }

    private fun showAlertForScheduleMeeting() {
        Alert.showAlert(activity, "Error", "Failed to schedule meeting", "Retry") { didPressSchedule() }
    }

    private fun showAlertForCreatingMeetingMessage(googleMeeting: GoogleMeeting) {
        Alert.showAlert(activity, "Error", "Failed to schedule meeting", "Retry") { createMessageForMeeting(googleMeeting) }
    }

    companion object {

        private const val USER_ID_ARG = "userIDArg"
        private const val CONVERSATION_ID_ARG = "conversationIDArg"

        private val TAG = ScheduleMeetingDialogFragment::class.java.simpleName

        fun newInstance(userId: Int, conversationId: Int): ScheduleMeetingDialogFragment {
            val fragment = ScheduleMeetingDialogFragment()
            val args = Bundle()
            args.putInt(USER_ID_ARG, userId)
            args.putInt(CONVERSATION_ID_ARG, conversationId)
            fragment.arguments = args
            return fragment
        }
    }
}
