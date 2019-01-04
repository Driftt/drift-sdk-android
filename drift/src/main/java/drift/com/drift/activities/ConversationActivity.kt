package drift.com.drift.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList

import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import drift.com.drift.R
import drift.com.drift.adapters.ConversationAdapter
import drift.com.drift.fragments.ScheduleMeetingDialogFragment
import drift.com.drift.helpers.Alert
import drift.com.drift.helpers.ClickListener
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.DownloadHelper
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.MessageReadHelper
import drift.com.drift.helpers.RecyclerTouchListener
import drift.com.drift.helpers.StatusBarColorizer
import drift.com.drift.helpers.UserPopulationHelper
import drift.com.drift.managers.AttachmentManager
import drift.com.drift.managers.MessageManager
import drift.com.drift.model.Attachment
import drift.com.drift.model.Auth
import drift.com.drift.model.Configuration
import drift.com.drift.model.Embed
import drift.com.drift.model.Message
import drift.com.drift.model.MessageRequest
import drift.com.drift.model.User
import drift.com.drift.wrappers.APICallbackWrapper
import drift.com.drift.wrappers.AttachmentCallback

class ConversationActivity : DriftActivity(), AttachmentCallback {


    internal var textEntryEditText: EditText
    internal var sendButtonImageView: ImageView
    internal var plusButtonImageView: ImageView
    internal var recyclerView: RecyclerView
    internal var statusTextView: TextView
    internal var driftWelcomeMessage: TextView
    internal var driftWelcomeImageView: ImageView
    internal var welcomeMessageLinearLayout: LinearLayout

    internal var driftBrandTextView: TextView

    internal var userForWelcomeMessage: User? = null
    internal var welcomeMessage: String

    internal var progressBar: ProgressBar

    internal var conversationAdapter: ConversationAdapter

    private val downloadReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            //check if the broadcast message is for our enqueued download
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DownloadHelper.instance.isDownloadFromApp(referenceId)) {
                Toast.makeText(this@ConversationActivity, "Download Complete", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var conversationId = -1
    private var endUserId: Long? = -1L
    private var conversationType = ConversationType.CONTINUE

    private enum class ConversationType {
        CREATE, CONTINUE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drift_sdk_activity_conversation)
        StatusBarColorizer.setActivityColor(this)

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)

        textEntryEditText = findViewById(R.id.drift_sdk_conversation_activity_edit_text)

        sendButtonImageView = findViewById(R.id.drift_sdk_conversation_activity_send_button)
        plusButtonImageView = findViewById(R.id.drift_sdk_conversation_activity_plus_button)
        recyclerView = findViewById(R.id.drift_sdk_conversation_activity_recycler_activity)
        statusTextView = findViewById(R.id.drift_sdk_conversation_activity_status_view)
        progressBar = findViewById(R.id.drift_sdk_conversation_activity_progress_view)
        driftWelcomeMessage = findViewById(R.id.drift_sdk_conversation_activity_welcome_text_view)
        driftWelcomeImageView = findViewById(R.id.drift_sdk_conversation_activity_welcome_image_view)
        driftBrandTextView = findViewById(R.id.drift_sdk_conversation_activity_drift_brand_text_view)
        welcomeMessageLinearLayout = findViewById(R.id.drift_sdk_conversation_activity_welcome_linear_layout)

        val intent = intent

        if (intent.extras != null) {
            conversationId = intent.extras!!.getInt(CONVERSATION_ID, -1)
            conversationType = intent.extras!!.getSerializable(CONVERSATION_TYPE) as ConversationType
        }


        if (conversationId == -1 && conversationType == ConversationType.CONTINUE) {
            Toast.makeText(this, "Invalid Conversation Id", Toast.LENGTH_SHORT).show()
            finish()
        }

        val auth = Auth.instance
        if (auth != null && auth!!.endUser != null) {
            endUserId = auth!!.endUser!!.id
        } else {
            //No Auth
            Toast.makeText(this, "We're sorry, an unknown error occurred", Toast.LENGTH_LONG).show()
            finish()
        }

        sendButtonImageView.setBackgroundColor(ColorHelper.backgroundColor)

        val actionBar = supportActionBar
        actionBar?.setTitle("Conversation")

        sendButtonImageView.setOnClickListener { didPressSendButton() }

        textEntryEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                didPressSendButton()
                return@OnEditorActionListener true
            }
            false
        })

        textEntryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.length != 0) {
                    sendButtonImageView.visibility = View.VISIBLE
                } else {
                    sendButtonImageView.visibility = View.GONE
                }
            }
        })

        AttachmentManager.instance.setAttachmentLoadHandle(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this, recyclerView, ClickListener { view, position ->
            val message = conversationAdapter.getItemAt(position)
            if (message != null && message.sendStatus == Message.SendStatus.FAILED) {
                resendMessage(message)
            }
        }))

        conversationAdapter = ConversationAdapter(this, MessageManager.instance.getMessagesForConversationId(conversationId))
        recyclerView.adapter = conversationAdapter

        if (conversationAdapter.itemCount == 0) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        AttachmentManager.instance.removeAttachmentLoadHandle()
        unregisterReceiver(downloadReceiver)

    }

    override fun refreshData() {
        super.refreshData()

        statusTextView.visibility = View.GONE

        updateForConversationType()

        if (conversationAdapter.itemCount == 0 && conversationType == ConversationType.CONTINUE) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }

        if (conversationId != -1) {

            MessageManager.instance.getMessagesForConversation(conversationId, APICallbackWrapper<ArrayList<Message>> { response ->
                if (response != null) {
                    progressBar.visibility = View.GONE
                    LoggerHelper.logMessage(TAG, response.toString())
                    conversationAdapter.updateData(response)

                    if (!response.isEmpty()) {
                        val message = response[0]
                        MessageReadHelper.markMessageAsReadAlongWithPrevious(message)
                    }

                    val attachmentIds = ArrayList<Int>()

                    for (message in response) {

                        if (message.attachmentIds != null) {
                            attachmentIds.addAll(message.attachmentIds!!)
                        }
                    }

                    AttachmentManager.instance.loadAttachments(attachmentIds)

                } else {
                    LoggerHelper.logMessage(TAG, "Failed to load users")
                }
            })
        }
    }

    internal fun updateForConversationType() {
        when (conversationType) {
            ConversationActivity.ConversationType.CREATE -> {

                val embed = Embed.instance
                if (embed != null && embed!!.configuration != null) {
                    if (embed!!.configuration!!.isOrgCurrentlyOpen) {
                        welcomeMessage = embed!!.configuration!!.theme!!.getWelcomeMessage()
                        driftWelcomeMessage.setText(embed!!.configuration!!.theme!!.getWelcomeMessage())
                    } else {
                        welcomeMessage = embed!!.configuration!!.theme!!.getAwayMessage()
                        driftWelcomeMessage.setText(embed!!.configuration!!.theme!!.getAwayMessage())
                    }
                    updateWelcomeImage(embed!!.configuration)

                    if (embed!!.configuration!!.showBranding!!) {
                        driftBrandTextView.visibility = View.VISIBLE
                    } else {
                        driftBrandTextView.visibility = View.GONE
                    }

                }
                welcomeMessageLinearLayout.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
            ConversationActivity.ConversationType.CONTINUE -> welcomeMessageLinearLayout.visibility = View.GONE
        }
    }

    fun updateWelcomeImage(configuration: Configuration?) {


        if (userForWelcomeMessage == null) {

            userForWelcomeMessage = configuration!!.userForWelcomeMessage

            if (userForWelcomeMessage != null) {
                UserPopulationHelper.populateTextAndImageFromUser(this, userForWelcomeMessage, null, driftWelcomeImageView)
            }
        }
    }

    override fun networkNotAvailable() {
        super.networkNotAvailable()
        statusTextView.visibility = View.VISIBLE
    }

    internal fun didPressSendButton() {

        when (conversationType) {
            ConversationActivity.ConversationType.CONTINUE -> sendMessage()
            ConversationActivity.ConversationType.CREATE -> createConversation()
        }
        textEntryEditText.setText("")
    }

    fun didReceiveNewMessage(message: Message) {


        val auth = Auth.instance
        if (auth != null && message.authorId === auth!!.endUser!!.id && message.contentType == "CHAT" && (message.attributes == null || message.attributes!!.appointmentInfo == null) && !message.fakeMessage) {
            LoggerHelper.logMessage(TAG, "Ignoring own message")
            return
        }


        val originalCount = conversationAdapter.itemCount
        val newMessages = MessageManager.instance.addMessageToConversation(conversationId, message)
        if (newMessages.size == originalCount + 1 && (message.attributes == null || message.attributes!!.appointmentInfo == null)) {
            conversationAdapter.updateDataAddingInOneMessage(newMessages, recyclerView)
        } else {
            conversationAdapter.updateData(newMessages)
        }

        MessageReadHelper.markMessageAsReadAlongWithPrevious(message)
    }

    internal fun sendMessage() {

        val messageRequest = MessageRequest(textEntryEditText.text.toString(), endUserId, null, this)
        val message = messageRequest.messageFromRequest(conversationId)
        conversationAdapter.addMessage(recyclerView, message)
        sendMessageRequest(messageRequest, message)
    }


    internal fun resendMessage(message: Message?) {

        val messageRequest = MessageRequest(message!!.body, endUserId, null, this)
        sendMessageRequest(messageRequest, message)

    }

    internal fun sendMessageRequest(messageRequest: MessageRequest, message: Message) {
        message.sendStatus = Message.SendStatus.SENDING
        conversationAdapter.updateMessage(message)

        MessageManager.instance.sendMessageForConversationId(conversationId, messageRequest, APICallbackWrapper<Message> { response ->
            if (response != null) {
                message.sendStatus = Message.SendStatus.SENT
                MessageManager.instance.removeMessageFromFailedCache(message, conversationId)
                conversationAdapter.updateMessage(message)
            } else {
                message.sendStatus = Message.SendStatus.FAILED
                MessageManager.instance.addMessageFailedToConversation(message, conversationId)
                conversationAdapter.updateData(MessageManager.instance.getMessagesForConversationId(conversationId))
            }
        })
    }

    internal fun createConversation() {

        val textToSend = textEntryEditText.text.toString()
        val messageRequest = MessageRequest(textToSend, endUserId, null, this)
        val message = messageRequest.messageFromRequest(conversationId)

        progressBar.visibility = View.VISIBLE

        var welcomeUserId: Int? = null
        if (userForWelcomeMessage != null) {
            welcomeUserId = userForWelcomeMessage!!.id
        }

        MessageManager.instance.createConversation(textToSend, welcomeMessage, welcomeUserId, APICallbackWrapper<Message> { response ->
            progressBar.visibility = View.GONE

            if (response != null) {
                conversationId = response.conversationId!!
                conversationType = ConversationType.CONTINUE

                message.sendStatus = Message.SendStatus.SENT
                conversationAdapter.addMessage(recyclerView, message)
                updateForConversationType()
                refreshData()
            } else {
                conversationAdapter.updateData(ArrayList())
                textEntryEditText.setText(textToSend)
                Alert.showAlert(this@ConversationActivity, "Error", "Failed to create conversation", "Retry") { didPressSendButton() }
            }
        })
    }

    override fun didLoadAttachments(attachments: ArrayList<Attachment>?) {
        if (attachments == null) {
            return
        }

        LoggerHelper.logMessage(TAG, "Did load attachments: " + attachments.size)
        conversationAdapter.updateForAttachments(attachments)
    }

    fun didPressScheduleMeetingFor(userId: Int) {
        ScheduleMeetingDialogFragment.newInstance(userId, conversationId).show(supportFragmentManager, ScheduleMeetingDialogFragment::class.java.simpleName)
    }

    companion object {

        private val TAG = ConversationActivity::class.java.simpleName
        private val CONVERSATION_ID = "DRIFT_CONVERSATION_ID_PARAM"
        private val CONVERSATION_TYPE = "DRIFT_CONVERSATION_TYPE_PARAM"

        fun intentForConversation(context: Context, conversationId: Int): Intent {

            val data = Bundle()
            data.putInt(CONVERSATION_ID, conversationId)
            data.putSerializable(CONVERSATION_TYPE, ConversationType.CONTINUE)

            return Intent(context, ConversationActivity::class.java)
                    .putExtras(data)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        fun intentForCreateConversation(context: Context): Intent {

            val data = Bundle()
            data.putSerializable(CONVERSATION_TYPE, ConversationType.CREATE)


            return Intent(context, ConversationActivity::class.java)
                    .putExtras(data)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        fun showCreateConversationFromContext(context: Context) {
            val intent = intentForCreateConversation(context)
            context.startActivity(intent)
        }
    }
}
