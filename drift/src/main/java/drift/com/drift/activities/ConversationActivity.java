package drift.com.drift.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import drift.com.drift.R;
import drift.com.drift.adapters.ConversationAdapter;
import drift.com.drift.fragments.ScheduleMeetingDialogFragment;
import drift.com.drift.helpers.Alert;
import drift.com.drift.helpers.ClickListener;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.DownloadHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.MessageReadHelper;
import drift.com.drift.helpers.RecyclerTouchListener;
import drift.com.drift.helpers.StatusBarColorizer;
import drift.com.drift.helpers.UserPopulationHelper;
import drift.com.drift.managers.AttachmentManager;
import drift.com.drift.managers.MessageManager;
import drift.com.drift.model.Attachment;
import drift.com.drift.model.Auth;
import drift.com.drift.model.Configuration;
import drift.com.drift.model.Embed;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageRequest;
import drift.com.drift.model.User;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.AttachmentCallback;

public class ConversationActivity extends DriftActivity implements AttachmentCallback {

    private static String TAG = ConversationActivity.class.getSimpleName();
    private static String CONVERSATION_ID = "DRIFT_CONVERSATION_ID_PARAM";
    private static String CONVERSATION_TYPE = "DRIFT_CONVERSATION_TYPE_PARAM";

    private enum ConversationType { CREATE, CONTINUE}


    EditText textEntryEditText;
    ImageView sendButtonImageView;
    ImageView plusButtonImageView;
    RecyclerView recyclerView;
    TextView statusTextView;
    TextView driftWelcomeMessage;
    ImageView driftWelcomeImageView;
    LinearLayout welcomeMessageLinearLayout;

    TextView driftBrandTextView;

    @Nullable
    User userForWelcomeMessage;
    String welcomeMessage;

    ProgressBar progressBar;

    ConversationAdapter conversationAdapter;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(DownloadHelper.getInstance().isDownloadFromApp(referenceId)) {
                Toast.makeText(ConversationActivity.this, "Download Complete", Toast.LENGTH_LONG).show();
            }
        }
    };

    public static Intent intentForConversation(Context context, int conversationId) {

        Bundle data = new Bundle();
        data.putInt(CONVERSATION_ID, conversationId);
        data.putSerializable(CONVERSATION_TYPE, ConversationType.CONTINUE);

        return new Intent(context, ConversationActivity.class)
                .putExtras(data)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent intentForCreateConversation(Context context) {

        Bundle data = new Bundle();
        data.putSerializable(CONVERSATION_TYPE, ConversationType.CREATE);


        return new Intent(context, ConversationActivity.class)
                .putExtras(data)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void showCreateConversationFromContext(Context context) {
        Intent intent = intentForCreateConversation(context);
        context.startActivity(intent);
    }

    private int conversationId = -1;
    private Long endUserId = -1L;
    private ConversationType conversationType = ConversationType.CONTINUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drift_sdk_activity_conversation);
        StatusBarColorizer.setActivityColor(this);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        textEntryEditText = findViewById(R.id.drift_sdk_conversation_activity_edit_text);

        sendButtonImageView = findViewById(R.id.drift_sdk_conversation_activity_send_button);
        plusButtonImageView =  findViewById(R.id.drift_sdk_conversation_activity_plus_button);
        recyclerView = findViewById(R.id.drift_sdk_conversation_activity_recycler_activity);
        statusTextView = findViewById(R.id.drift_sdk_conversation_activity_status_view);
        progressBar = findViewById(R.id.drift_sdk_conversation_activity_progress_view);
        driftWelcomeMessage = findViewById(R.id.drift_sdk_conversation_activity_welcome_text_view);
        driftWelcomeImageView = findViewById(R.id.drift_sdk_conversation_activity_welcome_image_view);
        driftBrandTextView = findViewById(R.id.drift_sdk_conversation_activity_drift_brand_text_view);
        welcomeMessageLinearLayout = findViewById(R.id.drift_sdk_conversation_activity_welcome_linear_layout);

        Intent intent = getIntent();

        if ( intent.getExtras() != null ) {
            conversationId = intent.getExtras().getInt(CONVERSATION_ID, -1);
            conversationType = (ConversationType) intent.getExtras().getSerializable(CONVERSATION_TYPE);
        }


        if(conversationId == -1 && conversationType == ConversationType.CONTINUE) {
            Toast.makeText(this, "Invalid Conversation Id", Toast.LENGTH_SHORT).show();
            finish();
        }

        Auth auth = Auth.getInstance();
        if (auth != null && auth.endUser != null) {
            endUserId = auth.endUser.id;
        } else{
            //No Auth
            Toast.makeText(this, "We're sorry, an unknown error occurred", Toast.LENGTH_LONG).show();
            finish();
        }

        sendButtonImageView.setBackgroundColor(ColorHelper.getBackgroundColor());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Conversation");
        }

        sendButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didPressSendButton();
            }
        });

        textEntryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    didPressSendButton();
                    return true;
                }
                return false;
            }
        });

        textEntryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    sendButtonImageView.setVisibility(View.VISIBLE);
                }else{
                    sendButtonImageView.setVisibility(View.GONE);
                }
            }
        });

        AttachmentManager.getInstance().setAttachmentLoadHandle(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Message message = conversationAdapter.getItemAt(position);
                if (message != null && message.sendStatus == Message.SendStatus.FAILED){
                    resendMessage(message);
                }
            }
        }));

        conversationAdapter = new ConversationAdapter(this, MessageManager.getInstance().getMessagesForConversationId(conversationId));
        recyclerView.setAdapter(conversationAdapter);

        if (conversationAdapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AttachmentManager.getInstance().removeAttachmentLoadHandle();
        unregisterReceiver(downloadReceiver);

    }

    @Override
    public void refreshData() {
        super.refreshData();

        statusTextView.setVisibility(View.GONE);

        updateForConversationType();

        if (conversationAdapter.getItemCount() == 0 && conversationType == ConversationType.CONTINUE) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (conversationId != -1) {

            MessageManager.getInstance().getMessagesForConversation(conversationId, new APICallbackWrapper<ArrayList<Message>>() {
                @Override
                public void onResponse(ArrayList<Message> response) {
                    if (response != null) {
                        progressBar.setVisibility(View.GONE);
                        LoggerHelper.logMessage(TAG, response.toString());
                        conversationAdapter.updateData(response);

                        if (!response.isEmpty()) {
                            Message message = response.get(0);
                            MessageReadHelper.markMessageAsReadAlongWithPrevious(message);
                        }

                        ArrayList<Integer> attachmentIds = new ArrayList<>();

                        for (Message message : response) {

                            if (message.attachmentIds != null) {
                                attachmentIds.addAll(message.attachmentIds);
                            }
                        }

                        AttachmentManager.getInstance().loadAttachments(attachmentIds);

                    } else {
                        LoggerHelper.logMessage(TAG, "Failed to load users");
                    }
                }
            });
        }
    }

    void updateForConversationType(){
        switch (conversationType) {
            case CREATE:

                Embed embed = Embed.getInstance();
                if (embed != null && embed.configuration != null){
                    if (embed.configuration.isOrgCurrentlyOpen()) {
                        welcomeMessage = embed.configuration.theme.getWelcomeMessage();
                        driftWelcomeMessage.setText(embed.configuration.theme.getWelcomeMessage());
                    } else {
                        welcomeMessage = embed.configuration.theme.getAwayMessage();
                        driftWelcomeMessage.setText(embed.configuration.theme.getAwayMessage());
                    }
                    updateWelcomeImage(embed.configuration);

                    if (embed.configuration.showBranding) {
                        driftBrandTextView.setVisibility(View.VISIBLE);
                    } else {
                        driftBrandTextView.setVisibility(View.GONE);
                    }

                }
                welcomeMessageLinearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;
            case CONTINUE:
                welcomeMessageLinearLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void updateWelcomeImage(Configuration configuration) {


        if (userForWelcomeMessage == null) {

            userForWelcomeMessage = configuration.getUserForWelcomeMessage();

            if (userForWelcomeMessage != null) {
                UserPopulationHelper.populateTextAndImageFromUser(this, userForWelcomeMessage, null, driftWelcomeImageView);
            }
        }
    }

    @Override
    public void networkNotAvailable() {
        super.networkNotAvailable();
        statusTextView.setVisibility(View.VISIBLE);
    }

    void didPressSendButton(){

        switch (conversationType) {
            case CONTINUE:
                sendMessage();
                break;
            case CREATE:
                createConversation();
                break;
        }
        textEntryEditText.setText("");
    }

    public void didReceiveNewMessage(Message message) {


        Auth auth = Auth.getInstance();
        if (auth != null && message.authorId.equals(auth.endUser.id) && message.contentType.equals("CHAT") && (message.attributes == null || message.attributes.appointmentInfo == null) && !message.fakeMessage){
            LoggerHelper.logMessage(TAG, "Ignoring own message");
            return;
        }


        int originalCount = conversationAdapter.getItemCount();
        ArrayList<Message> newMessages = MessageManager.getInstance().addMessageToConversation(conversationId, message);
        if (newMessages.size() == originalCount + 1 && (message.attributes == null || message.attributes.appointmentInfo == null)) {
            conversationAdapter.updateDataAddingInOneMessage(newMessages, recyclerView);
        } else {
            conversationAdapter.updateData(newMessages);
        }

        MessageReadHelper.markMessageAsReadAlongWithPrevious(message);
    }

    void sendMessage() {

        MessageRequest messageRequest = new MessageRequest(textEntryEditText.getText().toString(), endUserId, null, this);
        final Message message = messageRequest.messageFromRequest(conversationId);
        conversationAdapter.addMessage(recyclerView, message);
        sendMessageRequest(messageRequest, message);
    }


    void resendMessage(final Message message) {

        MessageRequest messageRequest = new MessageRequest(message.body, endUserId, null, this);
        sendMessageRequest(messageRequest, message);

    }

    void sendMessageRequest(MessageRequest messageRequest, final Message message) {
        message.sendStatus = Message.SendStatus.SENDING;
        conversationAdapter.updateMessage(message);

        MessageManager.getInstance().sendMessageForConversationId(conversationId, messageRequest, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                if (response != null) {
                    message.sendStatus = Message.SendStatus.SENT;
                    MessageManager.getInstance().removeMessageFromFailedCache(message, conversationId);
                    conversationAdapter.updateMessage(message);
                } else {
                    message.sendStatus = Message.SendStatus.FAILED;
                    MessageManager.getInstance().addMessageFailedToConversation(message, conversationId);
                    conversationAdapter.updateData(MessageManager.getInstance().getMessagesForConversationId(conversationId));
                }
            }
        });
    }

    void createConversation() {

        final String textToSend = textEntryEditText.getText().toString();
        final MessageRequest messageRequest = new MessageRequest(textToSend, endUserId, null, this);
        final Message message = messageRequest.messageFromRequest(conversationId);

        progressBar.setVisibility(View.VISIBLE);

        Long welcomeUserId = null;
        if (userForWelcomeMessage != null){
            welcomeUserId = userForWelcomeMessage.id;
        }

        MessageManager.getInstance().createConversation(textToSend, welcomeMessage, welcomeUserId, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                progressBar.setVisibility(View.GONE);

                if (response != null) {
                    conversationId = response.conversationId;
                    conversationType = ConversationType.CONTINUE;

                    message.sendStatus = Message.SendStatus.SENT;
                    conversationAdapter.addMessage(recyclerView, message);
                    updateForConversationType();
                    refreshData();
                } else {
                    conversationAdapter.updateData(new ArrayList<Message>());
                    textEntryEditText.setText(textToSend);
                    Alert.showAlert(ConversationActivity.this, "Error", "Failed to create conversation", "Retry", new Runnable() {
                        @Override
                        public void run() {
                            didPressSendButton();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void didLoadAttachments(ArrayList<Attachment> attachments) {
        if (attachments == null) {
            return;
        }

        LoggerHelper.logMessage(TAG, "Did load attachments: " + attachments.size());
        conversationAdapter.updateForAttachments(attachments);
    }

    public void didPressScheduleMeetingFor(Long userId) {
        ScheduleMeetingDialogFragment.newInstance(userId, conversationId).show(getSupportFragmentManager(), ScheduleMeetingDialogFragment.class.getSimpleName());
    }
}
