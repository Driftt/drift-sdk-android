package drift.com.drift.adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import drift.com.drift.R;
import drift.com.drift.activities.ConversationActivity;
import drift.com.drift.activities.ImageViewerActivity;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.DateHelper;
import drift.com.drift.managers.AttachmentManager;
import drift.com.drift.managers.UserManager;
import drift.com.drift.model.AppointmentInfo;
import drift.com.drift.model.Attachment;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageAttributes;
import drift.com.drift.model.User;
import drift.com.drift.views.AttachmentView;


public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static class CellTypes {
        static final int MESSAGE = 0;
        static final int MEETING_CELL = 1;
    }

    private List<Message> messages;
    private ConversationActivity activity;

    public ConversationAdapter(ConversationActivity activity, List<Message> messages) {
        if (messages == null) {
            this.messages = new ArrayList<>();
        } else {
            this.messages = messages;
        }
        this.activity = activity;
    }

    public void updateData(ArrayList<Message> messages) {
        if (messages == null) {
            return;
        }
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void updateDataAddingInOneMessage(ArrayList<Message> messages, RecyclerView recyclerView){
        if (messages == null) {
            return;
        }
        this.messages = messages;
        notifyItemInserted(0);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (linearLayoutManager.findFirstVisibleItemPosition() < 1) {
                recyclerView.scrollToPosition(0);
            }
        }
    }

    public void addMessage(RecyclerView recyclerView, Message message) {

        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(0, message);
        notifyItemInserted(0);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (linearLayoutManager.findFirstVisibleItemPosition() < 1) {
                recyclerView.scrollToPosition(0);
            }
        }
    }

    public void updateForAttachments(ArrayList<Attachment> attachments) {
        notifyDataSetChanged();
    }

    public void updateMessage(Message message) {

        if (messages == null) {
            return;
        }

        int index = messages.indexOf(message);

        if ( index != -1 ) {

            messages.set(index, message);
            notifyItemChanged(index);
        }
    }

    @Override
    public int getItemViewType(int position) {

        Message message = getItemAt(position);

        if (message.attributes != null && message.attributes.appointmentInfo != null) {
            return CellTypes.MEETING_CELL;
        }

        return CellTypes.MESSAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CellTypes.MESSAGE:
                return new MessageCell(inflater.inflate(R.layout.drift_sdk_conversation_message_cell, parent, false));
            case CellTypes.MEETING_CELL:
                return new MeetingCell(inflater.inflate(R.layout.drift_sdk_conversation_meeting_cell, parent, false));
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        boolean showDayHeader = true;
        final Message message = getItemAt(position);
        if (position < getItemCount() - 1){
            Message pastMessage = getItemAt(position + 1);

            if (message.createdAt == null || pastMessage.createdAt == null){
                Log.d("TAG", "Null Created At");

            } else {
                showDayHeader = !DateHelper.isSameDay(message.createdAt, pastMessage.createdAt);
            }
        }

        if (holder.getItemViewType() == CellTypes.MESSAGE) {
            MessageCell messageCell = (MessageCell) holder;
            messageCell.setupForMessage(message, showDayHeader);
            messageCell.meetingScheduleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageAttributes attributes = message.attributes;
                    if (attributes != null && attributes.presentSchedule != null)
                    activity.didPressScheduleMeetingFor(attributes.presentSchedule);
                }
            });
        }else if (holder.getItemViewType() == CellTypes.MEETING_CELL){
            MeetingCell meetingCell = (MeetingCell) holder;
            meetingCell.setupForMessage(message, showDayHeader);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public Message getItemAt(int position) {
        return messages.get(position);
    }


    class MessageCell extends RecyclerView.ViewHolder {

        LinearLayout dayHeader;
        TextView dayHeaderTextView;

        ImageView userImageView;
        TextView userTextView;
        TextView timeTextView;
        TextView mainTextView;

        ImageView singleAttachmentImageView;
        LinearLayout multipleAttachmentsLinearLayout;
        HorizontalScrollView multipleAttachmentScrollView;

        LinearLayout contentLinearLayout;


        RelativeLayout meetingViewRelativeLayout;
        ImageView meetingViewUserImageView;
        TextView meetingViewTextView;
        Button meetingScheduleButton;


        MessageCell(View view) {
            super(view);

            dayHeader = view.findViewById(R.id.drift_sdk_conversation_message_cell_day_header);
            dayHeaderTextView = dayHeader.findViewById(R.id.drift_sdk_conversation_day_divider_text_view);

            userImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_user_image_view);

            userTextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_user_text_view);
            timeTextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_time_text_view);
            mainTextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_main_text_view);

            singleAttachmentImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachment_image_view);
            multipleAttachmentsLinearLayout = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachments_linear_layout);
            multipleAttachmentScrollView = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachments_nested_scroll_view);

            contentLinearLayout = view.findViewById(R.id.drift_sdk_conversation_message_content_linear_layout);

            meetingViewRelativeLayout = view.findViewById(R.id.drift_sdk_conversation_message_cell_schedule_meeting_relative_layout);
            meetingViewUserImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_view_user_image_view);
            meetingViewTextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_view_text_view);
            meetingScheduleButton = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_button);
        }

        public void setupForMessage(final Message message, boolean showDayHeader){

            if (showDayHeader) {

                if (DateHelper.isSameDay(message.createdAt, new Date())){
                    dayHeaderTextView.setText("Today");
                }else {
                    String updatedTime = DateFormat.getDateFormat(activity).format(message.createdAt);
                    dayHeaderTextView.setText(updatedTime);
                }
                dayHeader.setVisibility(View.VISIBLE);
            }else{
                dayHeader.setVisibility(View.GONE);
            }


            if (message.attachmentIds != null && !message.attachmentIds.isEmpty()){


                ArrayList<Attachment> attachments = new ArrayList<>();

                for (Integer attachmentId : message.attachmentIds) {
                    Attachment attachment = AttachmentManager.getInstance().getAttachment(attachmentId);
                    if (attachment != null) {
                        attachments.add(attachment);
                    }
                }


                if (!attachments.isEmpty()) {

                    if (attachments.size() == 1) {
                        final Attachment attachment = attachments.get(0);
                        if (attachment.isImage()) {
                            final ArrayList<String> images = new ArrayList<>();

                            multipleAttachmentScrollView.setVisibility(View.GONE);
                            singleAttachmentImageView.setVisibility(View.VISIBLE);


                            images.add(attachment.generateDownloadURL());   ///Load full image when in viewer

                            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();


                            RequestOptions requestOptions = new RequestOptions()
                                    .centerCrop()
                                    .transform(new RoundedCorners((int) (4 * displayMetrics.density)))
                                    .placeholder(R.drawable.drift_sdk_attachment_placeholder);


                            Glide.with(activity)
                                    .load(attachment.getURL())
                                    .apply(requestOptions)
                                    .into(singleAttachmentImageView);

                            singleAttachmentImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = ImageViewerActivity.intentForUri(activity, attachment.getURL());
                                    activity.startActivity(intent);
                                }
                            });
                        } else {
                            singleAttachmentImageView.setVisibility(View.GONE);
                            multipleAttachmentsLinearLayout.removeAllViews();
                            AttachmentView attachmentView = new AttachmentView(activity);
                            attachmentView.setupForAttachment(attachment, message);
                            multipleAttachmentsLinearLayout.addView(attachmentView);
                            multipleAttachmentScrollView.setVisibility(View.VISIBLE);
                        }
                    }else{
                        singleAttachmentImageView.setVisibility(View.GONE);
                        multipleAttachmentsLinearLayout.removeAllViews();
                        for (Attachment attachment : attachments) {
                            AttachmentView attachmentView = new AttachmentView(activity);
                            attachmentView.setupForAttachment(attachment, message);
                            multipleAttachmentsLinearLayout.addView(attachmentView);
                        }
                        multipleAttachmentScrollView.setVisibility(View.VISIBLE);
                    }
                }else{
                    ///Load Attachments
                    singleAttachmentImageView.setVisibility(View.VISIBLE);
                    singleAttachmentImageView.setOnClickListener(null);
                    multipleAttachmentScrollView.setVisibility(View.GONE);
                }

            }else{
                singleAttachmentImageView.setVisibility(View.GONE);
                multipleAttachmentScrollView.setVisibility(View.GONE);
            }


            String timeStamp = DateFormat.getTimeFormat(activity).format(message.createdAt);
            timeTextView.setText(timeStamp);

            String body = message.getFormattedString();
            if (body == null || body.isEmpty()){
                mainTextView.setVisibility(View.GONE);
            } else {
                mainTextView.setVisibility(View.VISIBLE);
                mainTextView.setText(Html.fromHtml(body));
                mainTextView.setTextColor(ContextCompat.getColor(activity, R.color.drift_sdk_black));
            }
            mainTextView.setMovementMethod(LinkMovementMethod.getInstance());



            if (message.sendStatus != Message.SendStatus.SENT){
                timeTextView.setText(message.readableSendStatus());
            }

            if (message.attributes != null && message.attributes.presentSchedule != null){

                User user = UserManager.getInstance().userMap.get(message.attributes.presentSchedule);

                if(user != null){


                    RequestOptions requestOptions = new RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.drift_sdk_placeholder);

                    Glide.with(activity)
                            .load(user.avatarUrl)
                            .apply(requestOptions)
                            .into(meetingViewUserImageView);


                    meetingViewTextView.setText("Scheduling a Meeting with " + user.getUserName());
                }else{
                    Glide.with(activity).clear(meetingViewUserImageView);
                    meetingViewTextView.setText("Scheduling Meeting");
                }

                meetingViewRelativeLayout.setVisibility(View.VISIBLE);

                Drawable backgroundDrawable = DrawableCompat.wrap(meetingScheduleButton.getBackground()).mutate();
                DrawableCompat.setTint(backgroundDrawable, ColorHelper.getBackgroundColor());

                meetingScheduleButton.setTextColor(ColorHelper.getForegroundColor());

            }else{
                meetingViewRelativeLayout.setVisibility(View.GONE);
            }


            setupForUser(message, UserManager.getInstance().userMap.get(message.authorId));

        }

        void setupForUser(Message message, @Nullable User user) {

            Uri uriToLoad = null;
            Boolean ignoreUri = false;

            if (message.isMessageFromEndUser()){
                userTextView.setText("You");
            } else if (user != null) {
                userTextView.setText(user.getUserName());

                if (user.bot) {
                    ignoreUri = true;
                    Drawable placeholderDrawable = AppCompatResources.getDrawable(activity, R.drawable.drift_sdk_robot);
                    Drawable backgroundDrawable = AppCompatResources.getDrawable(activity, R.drawable.drift_sdk_bot_background);

                    DrawableCompat.setTint(backgroundDrawable, ColorHelper.getBackgroundColor());


                    Drawable[] layers = new Drawable[2];
                    layers[0] = backgroundDrawable;
                    layers[1] = placeholderDrawable;
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    userImageView.setImageDrawable(layerDrawable);


                } else if (user.avatarUrl != null) {
                    uriToLoad = Uri.parse(user.avatarUrl);
                }

            } else {
                //Unknown User
                userTextView.setText("Unknown User");
            }

            if (!ignoreUri) {

                RequestOptions requestOptions = new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.drift_sdk_placeholder);

                Glide.with(activity)
                    .load(uriToLoad)
                    .apply(requestOptions)
                    .into(userImageView);
            }
        }
    }

    class MeetingCell extends RecyclerView.ViewHolder {

        LinearLayout dayHeader;
        TextView dayHeaderTextView;

        ImageView meetingUserImage;
        TextView titleTextView;

        TextView meetingTimeTextView;
        TextView meetingDateTextView;
        TextView meetingTimeZoneTextView;


        MeetingCell(View view) {
            super(view);
            dayHeader = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_day_header);
            dayHeaderTextView = view.findViewById(R.id.drift_sdk_conversation_day_divider_text_view);
            meetingUserImage = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_user_image_view);
            titleTextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_title_text_view);

            meetingTimeTextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_time_text_view);
            meetingDateTextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_date_text_view);
            meetingTimeZoneTextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_timezone_text_view);
        }

        void setupForMessage(Message message, boolean showDayHeader) {
            if (showDayHeader) {
                dayHeader.setVisibility(View.VISIBLE);
                if (DateHelper.isSameDay(message.createdAt, new Date())){
                    dayHeaderTextView.setText("Today");
                }else {
                    String updatedTime = DateFormat.getDateFormat(activity).format(message.createdAt);
                    dayHeaderTextView.setText(updatedTime);
                }

            } else {
                dayHeader.setVisibility(View.GONE);
            }

            if (message.attributes != null) {
                if (message.attributes.appointmentInfo != null && message.attributes.appointmentInfo.agentId != null) {
                    AppointmentInfo appointmentInformation = message.attributes.appointmentInfo;

                    User user = UserManager.getInstance().userMap.get(appointmentInformation.agentId);

                    if (user != null) {
                        RequestOptions requestOptions = new RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.drift_sdk_placeholder);
                        Glide.with(activity).load(user.avatarUrl).apply(requestOptions).into(meetingUserImage);

                        titleTextView.setText("Scheduled a Meeting with " + user.getUserName());
                    } else {
                        titleTextView.setText("Scheduled Meeting");
                        Glide.with(activity).clear(meetingUserImage);
                    }


                    Date startDate = appointmentInformation.availabilitySlot;
                    Date endDate = new Date(startDate.getTime() + appointmentInformation.slotDuration * 60000);

                    String startDateText = DateHelper.formatDateForScheduleTime(startDate);
                    String endDateText = DateHelper.formatDateForScheduleTime(endDate);

                    meetingTimeTextView.setText(startDateText + "-" + endDateText);

                    java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
                    meetingDateTextView.setText(dateFormat.format(startDate));
                    Calendar cal = Calendar.getInstance();
                    TimeZone tz = cal.getTimeZone();
                    meetingTimeZoneTextView.setText(tz.getID());

                }
            }
        }
    }

}
