package drift.com.drift.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import drift.com.drift.Drift;
import drift.com.drift.R;
import drift.com.drift.activities.ConversationActivity;
import drift.com.drift.activities.ConversationListActivity;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.MessageReadHelper;
import drift.com.drift.helpers.UserPopulationHelper;
import drift.com.drift.model.Auth;
import drift.com.drift.model.ConversationExtra;
import drift.com.drift.model.User;
import drift.com.drift.model.Message;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.UserManagerCallback;

/**
 * Created by eoin on 23/08/2017.
 */

public class PresentationManager {

    private static String TAG = PresentationManager.class.getSimpleName();

    private static PresentationManager _presentationManager = new PresentationManager();

    public static PresentationManager getInstance() {
        return _presentationManager;
    }

    @Nullable private PopupWindow pw;

    public void didReceiveNewMessage(Message message) {

        //Did Get new message, if conversation activity is shown and conversation id's match send message to that activity
        //Otherwise add message to conversation in conversation manager and set to unread.
        //If no conversation maybe refresh conversations? as its new

        //If not in conversation activity and popup not currently showing show popup

        ConversationManager.getInstance().manuallyAddUnreadCount();

        Activity activity = Drift.getCurrentActivity();

        if (activity instanceof ConversationActivity) {

            ConversationActivity conversationActivity = (ConversationActivity) activity;
            conversationActivity.didReceiveNewMessage(message);

        } else if (activity instanceof ConversationListActivity) {

            ConversationListActivity conversationListActivity = (ConversationListActivity) activity;
            conversationListActivity.didReceiveNewMessage();

        } else {

            showPopupForMessage(message, ConversationManager.getInstance().getUnreadCountForUser() - 1);

        }
    }

    public void checkForUnreadMessagesToShow(int orgId, final int endUserId){

        LoggerHelper.logMessage(TAG, "Checking for Messages to show");
        UserManager.getInstance().getUsersIfWeNeedTo(orgId, new UserManagerCallback() {
            @Override
            public void didLoadUsers(Boolean success) {

                ConversationManager.getInstance().getConversationsForEndUser(endUserId, new APICallbackWrapper<ArrayList<ConversationExtra>>() {
                    @Override
                    public void onResponse(ArrayList<ConversationExtra> response) {

                        if (response != null) {

                            showMessagePopupFromManager();

                        } else {
                            LoggerHelper.logMessage(TAG, "Failed to get conversation extras");
                        }
                    }
                });
            }
        });
    }

    public void checkWeNeedToReshowMessagePopover(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!ConversationManager.getInstance().getConversations().isEmpty()) {
                    showMessagePopupFromManager();
                }
            }
        }, 1000);

    }

    private void showMessagePopupFromManager(){

        ///Check for unread conversation extras
        ArrayList<Message> unreadMessages = new ArrayList<>();
        int unreadMessageCount = -1;
        for (ConversationExtra conversationExtra : ConversationManager.getInstance().getConversations()) {
            if (conversationExtra.unreadMessages != 0) {
                unreadMessages.add(conversationExtra.lastAgentMessage);
                unreadMessageCount += conversationExtra.unreadMessages;
            }
        }

        if (!unreadMessages.isEmpty()) {
            showPopupForMessage(unreadMessages.get(0), unreadMessageCount);
        }
    }

    void showPopupForMessage(final Message message, final int otherMessages) {

        User user = UserManager.getInstance().userMap.get(message.authorId);
        Auth auth = Auth.getInstance();
        if (user != null) {
            showPopupForMessage(user, message, otherMessages);
        } else if (auth != null && auth.endUser != null && auth.endUser.orgId != null){
            UserManager.getInstance().getUsers(auth.endUser.orgId, new UserManagerCallback() {
                @Override
                public void didLoadUsers(Boolean success) {
                    User user = UserManager.getInstance().userMap.get(message.authorId);
                    showPopupForMessage(user, message, otherMessages);
                }
            });
        }

    }


    private void showPopupForMessage(User user, final Message message, final int otherMessages) {

        try {

            final Activity activity= Drift.getCurrentActivity();

            if (message == null || activity == null) {
                return;
            }

            if (pw != null) {

                View view = pw.getContentView();
                LoggerHelper.logMessage(TAG, "View Visible: " + String.valueOf(view.isShown()));
                if (!view.isShown()) {
                    LoggerHelper.logMessage(TAG, "Popup activity finished, make a new one");
                } else {
                    //Already showing a new message. Maybe update unread count?
                    LoggerHelper.logMessage(TAG, "Popup still showing, we should update it");

                    TextView unreadTextView = (TextView) view.findViewById(R.id.drift_sdk_new_message_unread_text_view);
                    updateUnreadCount(unreadTextView, otherMessages);

                    return;
                }
            }


            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(drift.com.drift.R.layout.drift_sdk_new_message_view, null);


            Button closeButton = layout.findViewById(drift.com.drift.R.id.drift_sdk_new_message_close_button);
            Button openButton = layout.findViewById(R.id.drift_sdk_new_message_open_button);

            TextView titleTextView = layout.findViewById(R.id.drift_sdk_new_message_title_text_view);
            TextView subtitleTextView = layout.findViewById(R.id.drift_sdk_new_message_subtitle_text_view);
            ImageView userImageView = layout.findViewById(R.id.drift_sdk_new_message_user_image_view);

            TextView unreadTextView = layout.findViewById(R.id.drift_sdk_new_message_unread_text_view);

            LinearLayout bottomLinearLayout = layout.findViewById(R.id.drift_sdk_new_message_bottom_linear_layout);

            Drawable backgroundDrawable = DrawableCompat.wrap(bottomLinearLayout.getBackground()).mutate();
            DrawableCompat.setTint(backgroundDrawable, ColorHelper.getBackgroundColor());



            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageReadHelper.markMessageAsReadAlongWithPrevious(message);
                    closePopupView();
                }
            });


            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (otherMessages != 0) {
                        Drift.showConversationActivity();
                    } else {
                        int conversationId = message.conversationId;
                        Intent intent = ConversationActivity.intentForConversation(activity, conversationId);
                        activity.startActivity(intent);
                    }
                    closePopupView();
                }
            });

            UserPopulationHelper.populateTextAndImageFromUser(activity, user, titleTextView, userImageView);

            String text = message.getFormattedString();

            if (text == null || text.isEmpty() || text.trim().isEmpty()){
                subtitleTextView.setText(new String(Character.toChars(0x1F4CE))  + " [Attachment]");
            } else {
                subtitleTextView.setText(Html.fromHtml(text));
            }

            updateUnreadCount(unreadTextView, otherMessages);


            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

            int width = displayMetrics.widthPixels;

            width = Math.min(width, (int) (500 * displayMetrics.density));

            pw = new PopupWindow(layout, width, LinearLayout.LayoutParams.WRAP_CONTENT, false);
            pw.setAnimationStyle(R.style.Animation);

            pw.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            closePopupView();
        }
    }

    private void updateUnreadCount(TextView unreadCountTetView, int unreadMessages) {
        if (unreadMessages > 0) {
            if (unreadMessages > 9) {
                unreadCountTetView.setText("+9");
            } else {
                unreadCountTetView.setText(String.valueOf(unreadMessages));
            }
            unreadCountTetView.setVisibility(View.VISIBLE);
        } else {
            unreadCountTetView.setVisibility(View.INVISIBLE);
        }
    }

    public void closePopupView(){
        if (pw != null) {
            pw.dismiss();
            pw = null;
        }
    }
}
