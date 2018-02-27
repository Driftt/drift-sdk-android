package drift.com.drift.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import drift.com.drift.R;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.managers.UserManager;
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


    TextView headerTitleTextView;
    TextView headerDurationTextView;
    RelativeLayout headerRelativeLayout;

    TextView userTextView;
    ImageView userImageView;

    TextView titleTextView;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    LinearLayout confirmationLinearLayout;

    TextView confirmationTimeTextView;
    TextView confirmationDateTextView;
    TextView confirmationTimezoneTextView;
    Button confirmationButton;

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

        confirmationLinearLayout = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_linear_layout);

        confirmationTimeTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_time_text_view);
        confirmationDateTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_date_text_view);
        confirmationTimezoneTextView = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_timezone_text_view);

        confirmationButton = view.findViewById(R.id.drift_sdk_schedule_meeting_fragment_confirmation_button);

        confirmationLinearLayout.setVisibility(View.GONE);
        headerDurationTextView.setText("");

        headerRelativeLayout.setBackgroundColor(ColorHelper.getBackgroundColor());
        headerTitleTextView.setTextColor(ColorHelper.getForegroundColor());
        headerDurationTextView.setTextColor(ColorHelper.getForegroundColor());


        User user = UserManager.getInstance().userMap.get(userId);
        if (user != null){
            userTextView.setText(user.getUserName());
            RequestOptions requestOptions = new RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.placeholder);

            Glide.with(getActivity())
                    .load(user.avatarUrl)
                    .apply(requestOptions)
                    .into(userImageView);
        } else {
            userTextView.setText("");
            Glide.with(getActivity()).clear(userImageView);
        }

        setupAvailabilityCall();

        return view;
    }

    public void setupAvailabilityCall(){

        progressBar.setVisibility(View.VISIBLE);
        ScheduleMeetingWrapper.getUserAvailability(userId, new APICallbackWrapper<UserAvailability>() {
            @Override
            public void onResponse(UserAvailability response) {
                progressBar.setVisibility(View.GONE);
                LoggerHelper.logMessage(TAG, "Got Response");
            }
        });
    }
}
