package drift.com.drift.activities


import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.core.graphics.drawable.DrawableCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import drift.com.drift.R
import drift.com.drift.adapters.ConversationListAdapter
import drift.com.drift.helpers.Alert
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.RecyclerTouchListener
import drift.com.drift.helpers.StatusBarColorizer
import drift.com.drift.managers.ConversationManager
import drift.com.drift.managers.UserManager
import drift.com.drift.model.Auth

internal class ConversationListActivity : DriftActivity() {

    private lateinit var conversationRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var networkAvailabilityBar: TextView

    private lateinit var emptyState: LinearLayout
    private lateinit var emptyStateButton: Button

    private lateinit var conversationListAdapter: ConversationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarColorizer.setActivityColor(this)
        setContentView(R.layout.drift_sdk_activity_conversation_list)

        val actionBar = supportActionBar
        actionBar?.title = "Conversations"

        conversationRecyclerView = findViewById(R.id.drift_sdk_conversation_list_recycler_view)
        progressBar = findViewById(R.id.drift_sdk_conversation_list_progress_bar)
        networkAvailabilityBar = findViewById(R.id.drift_sdk_conversation_list_load_page_text_view)

        emptyState = findViewById(R.id.drift_sdk_conversation_list_empty_linear_layout)
        emptyStateButton = findViewById(R.id.drift_sdk_conversation_list_empty_state_create_button)

        val layoutManager = LinearLayoutManager(this)
        conversationRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(conversationRecyclerView.context,
                layoutManager.orientation)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.drift_sdk_recycler_view_divider)!!)
        conversationRecyclerView.addItemDecoration(dividerItemDecoration)

        conversationRecyclerView.addOnItemTouchListener(RecyclerTouchListener(this, conversationRecyclerView) { _, position ->
            val conversation = conversationListAdapter.getItemAt(position)
            if (conversation != null) {
                val intent = ConversationActivity.intentForConversation(this@ConversationListActivity, conversation.id!!)
                this@ConversationListActivity.startActivity(intent)
            }
        })

        emptyStateButton.setOnClickListener {
            val intent = ConversationActivity.intentForCreateConversation(this@ConversationListActivity)
            this@ConversationListActivity.startActivity(intent)
        }


        emptyStateButton.setTextColor(ColorHelper.foregroundColor)

        val backgroundDrawable = DrawableCompat.wrap(emptyStateButton.background).mutate()
        DrawableCompat.setTint(backgroundDrawable, ColorHelper.backgroundColor)


        conversationListAdapter = ConversationListAdapter(this, ConversationManager.conversations)
        conversationRecyclerView.adapter = conversationListAdapter

        if (conversationListAdapter.itemCount == 0) {
            if (ConversationManager.isApiCallComplete) {
                emptyState.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
            }
        } else {
            progressBar.visibility = View.GONE
            emptyState.visibility = View.GONE
        }

    }

    override fun refreshData() {
        super.refreshData()
        networkAvailabilityBar.visibility = View.GONE

        val auth = Auth.instance
        if (auth?.endUser != null) {
            fetchData(auth)
        }
    }

    private fun fetchData(auth: Auth) {
        ConversationManager.getConversationsForEndUser(auth.endUser!!.id) { response ->
            if (response != null) {
                progressBar.visibility = View.GONE
                LoggerHelper.logMessage(TAG, response.toString())

                if (response.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                } else {
                    emptyState.visibility = View.GONE
                }

                conversationListAdapter.updateDate(response)
            } else {
                Alert.showAlert(this@ConversationListActivity, applicationContext.getString(R.string.error_title),
                        applicationContext.getString(R.string.failed_to_load_conversations), applicationContext.getString(R.string.retry)) { refreshData() }
            }
        }
    }

    override fun networkNotAvailable() {
        super.networkNotAvailable()
        networkAvailabilityBar.visibility = View.VISIBLE
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.drift_sdk_create_conversaion_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.drift_sdk_create_conversation_action) {
            val intent = ConversationActivity.intentForCreateConversation(this@ConversationListActivity)
            this@ConversationListActivity.startActivity(intent)
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    fun didReceiveNewMessage() {
        refreshData()
    }

    companion object {

        private val TAG = ConversationListActivity::class.java.simpleName

        fun showFromContext(context: Context) {
            val intent = Intent(context, ConversationListActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
