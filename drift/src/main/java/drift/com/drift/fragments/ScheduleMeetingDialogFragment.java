package drift.com.drift.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import drift.com.drift.R;
import drift.com.drift.adapters.ScheduleMeetingAdapter;
import drift.com.drift.helpers.Alert;
import drift.com.drift.helpers.ClickListener;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.DateHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.RecyclerTouchListener;
import drift.com.drift.managers.MessageManager;
import drift.com.drift.managers.UserManager;
import drift.com.drift.model.GoogleMeeting;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageRequest;
import drift.com.drift.model.User;
import drift.com.drift.model.UserAvailability;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.ScheduleMeetingWrapper;


public class ScheduleMeetingDialogFragment extends DialogFragment {

    private static final String USER_ID_ARG = "userIDArg";
    private static final String CONVERSATION_ID_ARG = "conversationIDArg";

    private static final String TAG = ScheduleMeetingDialogFragment.class.getSimpleName();

    private int userId;
    private int conversationId;


    enum ScheduleMeetingState {
        DAY,
        TIME,
        CONFIRM
    }

    ScheduleMeetingState scheduleMeetingState = ScheduleMeetingState.DAY;

    @Nullable
    Date selectedDate;

    @Nullable
    Date selectedTime;

    @Nullable
    UserAvailability userAvailability;

    TextView headerTitleTextView;
    TextView headerDurationTextView;
    RelativeLayout headerRelativeLayout;

    TextView userTextView;
    ImageView userImageView;

    TextView titleTextView;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    ScrollView confirmationScrollView;

    TextView confirmationTimeTextView;
    TextView confirmationDateTextView;
    TextView confirmationTimezoneTextView;
    Button confirmationButton;

    ImageButton backChevron;

    ScheduleMeetingAdapter adapter;

    public ScheduleMeetingDialogFragment() {}

    public static ScheduleMeetingDialogFragment newInstance(int userId, int conversationId) {
        ScheduleMeetingDialogFragment fragment = new ScheduleMeetingDialogFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID_ARG, userId);
        args.putInt(CONVERSATION_ID_ARG, conversationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(USER_ID_ARG);
            conversationId = getArguments().getInt(CONVERSATION_ID_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule_meeting_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        headerTitleTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_header_title_text_view);
        headerDurationTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_duration_text_view);
        headerRelativeLayout = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_header_relative_layout);

        userTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_user_text_view);
        userImageView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_user_image_view);

        titleTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_title_text_view);

        recyclerView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_recycler_view);

        progressBar = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_progress_bar);

        confirmationScrollView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_scroll_view);

        confirmationTimeTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_time_text_view);
        confirmationDateTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_date_text_view);
        confirmationTimezoneTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_timezone_text_view);

        confirmationButton = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_button);

        backChevron = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_back_chevron);

        confirmationScrollView.setVisibility(View.GONE);
        headerDurationTextView.setText("");

        headerRelativeLayout.setBackgroundColor(ColorHelper.getBackgroundColor());
        headerTitleTextView.setTextColor(ColorHelper.getForegroundColor());
        headerDurationTextView.setTextColor(ColorHelper.getForegroundColor());
        backChevron.setColorFilter(ColorHelper.getBackgroundColor());


        adapter = new ScheduleMeetingAdapter();
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.drift_sdk_recycler_view_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Date chosenDate = adapter.getItemAt(position);
                if (chosenDate != null){

                    switch (scheduleMeetingState) {
                        case DAY:
                            selectedDate = chosenDate;
                            changeToState(ScheduleMeetingState.TIME);
                            break;
                        case TIME:
                            selectedTime = chosenDate;
                            changeToState(ScheduleMeetingState.CONFIRM);
                            break;

                        case CONFIRM://Can't hapen
                            break;
                    }


                }
            }
        }));

        backChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didPressBackChevron();
            }
        });

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didPressSchedule();
            }
        });

        User user = UserManager.getInstance().getUserForId(userId);
        if (user != null){
            userTextView.setText(user.getUserName());
            RequestOptions requestOptions = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.drift_sdk_placeholder);

            Glide.with(getActivity())
                    .load(user.avatarUrl)
                    .apply(requestOptions)
                    .into(userImageView);
        } else {
            userTextView.setText("");
            Glide.with(getActivity()).clear(userImageView);
        }

        setupAvailabilityCall();
        changeToState(ScheduleMeetingState.DAY);
        return view;
    }

    public void setupAvailabilityCall(){

        progressBar.setVisibility(View.VISIBLE);
        //TODO: Cancel call on back
        ScheduleMeetingWrapper.getUserAvailability(userId, new APICallbackWrapper<UserAvailability>() {
            @Override
            public void onResponse(UserAvailability response) {
                progressBar.setVisibility(View.GONE);

                if (response != null) {
                    userAvailability = response;
                    headerDurationTextView.setText(getResources().getString(R.string.drift_sdk_schedule_meeting_minutes, response.slotDuration));
                    changeToState(ScheduleMeetingState.DAY);
                } else {
                    showAlertForFailedToGetAvailability();
                }

            }
        });
    }

    public void setupForSelectedDate(Date date){


        recyclerView.setVisibility(View.GONE);
        confirmationScrollView.setVisibility(View.VISIBLE);


        confirmationDateTextView.setText(DateHelper.formatDateForScheduleDay(date));
        if (userAvailability != null) {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            confirmationTimezoneTextView.setText(tz.getID());
            final long ONE_MINUTE_IN_MILLIS = 60000;
            Date endDate = new Date(date.getTime() + (userAvailability.slotDuration * ONE_MINUTE_IN_MILLIS));

            confirmationTimeTextView.setText(getString(R.string.drift_sdk_dash_divided_strings, DateHelper.formatDateForScheduleTime(date), DateHelper.formatDateForScheduleTime(endDate)));

        } else {
            confirmationTimezoneTextView.setText("");
            confirmationTimeTextView.setText("");
        }

    }

    public void changeToState(ScheduleMeetingState state){

        scheduleMeetingState = state;

        switch (state){

            case CONFIRM:
                titleTextView.setText("");
                backChevron.setVisibility(View.VISIBLE);
                if (selectedTime != null) {
                    setupForSelectedDate(selectedTime);
                }
                break;
            case DAY:
                titleTextView.setText(R.string.drift_sdk_select_a_day);
                backChevron.setVisibility(View.INVISIBLE);
                if (userAvailability != null) {
                    adapter.setupForDates(userAvailability.getUniqueDates(), ScheduleMeetingAdapter.SelectionType.DAY);
                }
                recyclerView.setVisibility(View.VISIBLE);
                confirmationScrollView.setVisibility(View.GONE);
                break;
            case TIME:
                titleTextView.setText(R.string.drift_sdk_select_a_time);
                backChevron.setVisibility(View.VISIBLE);
                if (userAvailability != null && selectedDate != null) {
                    adapter.setupForDates(userAvailability.getDatesForDay(selectedDate), ScheduleMeetingAdapter.SelectionType.TIME);
                }
                recyclerView.setVisibility(View.VISIBLE);
                confirmationScrollView.setVisibility(View.GONE);
                break;
        }
    }

    public void didPressBackChevron(){

        switch (scheduleMeetingState){
            case CONFIRM:
                changeToState(ScheduleMeetingState.TIME);
                break;
            case TIME:
                changeToState(ScheduleMeetingState.DAY);
                break;
            case DAY:
                break;
        }
    }

    public void didPressSchedule() {

        progressBar.setVisibility(View.VISIBLE);

        ScheduleMeetingWrapper.scheduleMeeting(userId, conversationId, selectedTime.getTime(), new APICallbackWrapper<GoogleMeeting>() {
            @Override
            public void onResponse(GoogleMeeting response) {
                if (response != null) {
                    LoggerHelper.logMessage(TAG, response.toString());
                    createMessageForMeeting(response);
                } else {
                    progressBar.setVisibility(View.GONE);
                    showAlertForScheduleMeeting();
                }
            }
        });
    }

    public void createMessageForMeeting(final GoogleMeeting googleMeeting) {
        progressBar.setVisibility(View.VISIBLE);

        MessageRequest messageRequest = new MessageRequest(googleMeeting, userAvailability, userId, conversationId, selectedTime);

        MessageManager.getInstance().sendMessageForConversationId(conversationId, messageRequest, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {
                progressBar.setVisibility(View.GONE);

                if (response != null) {
                    dismiss();
                } else {
                    showAlertForCreatingMeetingMessage(googleMeeting);
                }
            }
        });
    }

    public void showAlertForFailedToGetAvailability(){
        Alert.showAlert(getActivity(), "Error", "Failed to load users availability", "Retry", new Runnable() {
            @Override
            public void run() {
                setupAvailabilityCall();
            }
        });
    }

    public void showAlertForScheduleMeeting(){
        Alert.showAlert(getActivity(), "Error", "Failed to schedule meeting", "Retry", new Runnable() {
            @Override
            public void run() {
                didPressSchedule();
            }
        });
    }

    public void showAlertForCreatingMeetingMessage(final GoogleMeeting googleMeeting){
        Alert.showAlert(getActivity(), "Error", "Failed to schedule meeting", "Retry", new Runnable() {
            @Override
            public void run() {
                createMessageForMeeting(googleMeeting);
            }
        });
    }
}
