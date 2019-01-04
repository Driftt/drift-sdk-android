package drift.com.drift.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import drift.com.drift.R;
import drift.com.drift.adapters.ConversationListAdapter;
import drift.com.drift.helpers.Alert;
import drift.com.drift.helpers.ClickListener;
import drift.com.drift.helpers.ColorHelper;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.helpers.RecyclerTouchListener;
import drift.com.drift.helpers.StatusBarColorizer;
import drift.com.drift.managers.ConversationManager;
import drift.com.drift.managers.UserManager;
import drift.com.drift.model.Auth;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.ConversationExtra;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.UserManagerCallback;

public class ConversationListActivity extends DriftActivity {

    private static String TAG = ConversationListActivity.class.getSimpleName();

    RecyclerView conversationRecyclerView;
    ProgressBar progressBar;
    TextView networkAvailabilityBar;

    LinearLayout emptyState;
    Button emptyStateButton;

    ConversationListAdapter conversationListAdapter;

    public static void showFromContext(Context context) {
        Intent intent = (new Intent(context, ConversationListActivity.class)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarColorizer.setActivityColor(this);
        setContentView(R.layout.drift_sdk_activity_conversation_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Conversations");
        }

        conversationRecyclerView = findViewById(R.id.drift_sdk_conversation_list_recycler_view);
        progressBar = findViewById(R.id.drift_sdk_conversation_list_progress_bar);
        networkAvailabilityBar =  findViewById(R.id.drift_sdk_conversation_list_load_page_text_view);

        emptyState = findViewById(R.id.drift_sdk_conversation_list_empty_linear_layout);
        emptyStateButton = findViewById(R.id.drift_sdk_conversation_list_empty_state_create_button);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(conversationRecyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.drift_sdk_recycler_view_divider));
        conversationRecyclerView.addItemDecoration(dividerItemDecoration);

        conversationRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, conversationRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Conversation conversation = conversationListAdapter.getItemAt(position);
                if (conversation != null){
                    Intent intent = ConversationActivity.intentForConversation(ConversationListActivity.this, conversation.id);
                    ConversationListActivity.this.startActivity(intent);
                }
            }
        }));

        emptyStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ConversationActivity.intentForCreateConversation(ConversationListActivity.this);
                ConversationListActivity.this.startActivity(intent);
            }
        });


        emptyStateButton.setTextColor(ColorHelper.getForegroundColor());

        Drawable backgroundDrawable = DrawableCompat.wrap(emptyStateButton.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, ColorHelper.getBackgroundColor());


        conversationListAdapter = new ConversationListAdapter(this, ConversationManager.getInstance().getConversations());
        conversationRecyclerView.setAdapter(conversationListAdapter);

        if (conversationListAdapter.getItemCount() == 0) {
            if (ConversationManager.getInstance().isApiCallComplete()) {
                emptyState.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        }

    }

    @Override
    public void refreshData() {
        super.refreshData();
        networkAvailabilityBar.setVisibility(View.GONE);

        final Auth auth = Auth.getInstance();
        if (auth != null && auth.endUser != null) {

            UserManager.getInstance().getUsers(auth.endUser.orgId, new UserManagerCallback() {
                @Override
                public void didLoadUsers(Boolean success) {
                    if (success) {
                        fetchData(auth);
                    } else {
                        LoggerHelper.logMessage(TAG, "Failed to load users");
                    }
                }
            });
        }
    }

    private void fetchData(Auth auth) {
        ConversationManager.getInstance().getConversationsForEndUser(auth.endUser.id, new APICallbackWrapper<ArrayList<ConversationExtra>>() {
            @Override
            public void onResponse(ArrayList<ConversationExtra> response) {
                if (response != null) {
                    progressBar.setVisibility(View.GONE);
                    LoggerHelper.logMessage(TAG, response.toString());

                    if (response.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                    }

                    conversationListAdapter.updateDate(response);
                } else {
                    Alert.showAlert(ConversationListActivity.this, getApplicationContext().getString(R.string.error_title),
                            getApplicationContext().getString(R.string.failed_to_load_conversations), getApplicationContext().getString(R.string.retry), new Runnable() {
                        @Override
                        public void run() {
                            refreshData();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void networkNotAvailable() {
        super.networkNotAvailable();
        networkAvailabilityBar.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drift_sdk_create_conversaion_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.drift_sdk_create_conversation_action) {
            Intent intent = ConversationActivity.intentForCreateConversation(ConversationListActivity.this);
            ConversationListActivity.this.startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void didReceiveNewMessage() {
        refreshData();
    }
}
