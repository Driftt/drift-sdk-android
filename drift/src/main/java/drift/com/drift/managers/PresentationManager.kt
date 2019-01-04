package drift.com.drift.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.core.graphics.drawable.DrawableCompat
import android.text.Html
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

import java.util.ArrayList
import drift.com.drift.Drift
import drift.com.drift.R
import drift.com.drift.activities.ConversationActivity
import drift.com.drift.activities.ConversationListActivity
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.helpers.MessageReadHelper
import drift.com.drift.helpers.UserPopulationHelper
import drift.com.drift.model.Auth
import drift.com.drift.model.ConversationExtra
import drift.com.drift.model.User
import drift.com.drift.model.Message
import drift.com.drift.wrappers.APICallbackWrapper
import drift.com.drift.wrappers.UserManagerCallback

/**
 * Created by eoin on 23/08/2017.
 */

class PresentationManager {

    private var pw: PopupWindow? = null

    internal fun didReceiveNewMessage(message: Message) {

        //Did Get new message, if conversation activity is shown and conversation id's match send message to that activity
        //Otherwise add message to conversation in conversation manager and set to unread.
        //If no conversation maybe refresh conversations? as its new

        //If not in conversation activity and popup not currently showing show popup

        ConversationManager.instance.manuallyAddUnreadCount()

        val activity = Drift.currentActivity

        if (activity is ConversationActivity) {

            val conversationActivity = activity as ConversationActivity
            conversationActivity.didReceiveNewMessage(message)

        } else if (activity is ConversationListActivity) {

            val conversationListActivity = activity as ConversationListActivity
            conversationListActivity.didReceiveNewMessage()

        } else if (message.authorType == "USER") {

            showPopupForMessage(message, ConversationManager.instance.unreadCountForUser - 1)

        }
    }

    fun checkForUnreadMessagesToShow(orgId: Int, endUserId: Long?) {

        LoggerHelper.logMessage(TAG, "Checking for Messages to show")
        UserManager.instance.getUsersIfWeNeedTo(orgId, UserManagerCallback {
            ConversationManager.instance.getConversationsForEndUser(endUserId, APICallbackWrapper<ArrayList<ConversationExtra>> { response ->
                if (response != null) {

                    showMessagePopupFromManager()

                } else {
                    LoggerHelper.logMessage(TAG, "Failed to get conversation extras")
                }
            })
        })
    }

    fun checkWeNeedToReshowMessagePopover() {

        val handler = Handler()
        handler.postDelayed({
            if (!ConversationManager.instance.conversations.isEmpty()) {
                showMessagePopupFromManager()
            }
        }, 1000)

    }

    private fun showMessagePopupFromManager() {

        ///Check for unread conversation extras
        val unreadMessages = ArrayList<Message>()
        var unreadMessageCount = -1
        for (conversationExtra in ConversationManager.instance.conversations) {
            if (conversationExtra.unreadMessages != 0) {
                if (conversationExtra.lastAgentMessage != null) {
                    unreadMessages.add(conversationExtra.lastAgentMessage)
                    unreadMessageCount += conversationExtra.unreadMessages!!
                }
            }
        }

        if (!unreadMessages.isEmpty()) {
            showPopupForMessage(unreadMessages[0], unreadMessageCount)
        }
    }

    private fun showPopupForMessage(message: Message, otherMessages: Int) {

        val user = UserManager.instance.getUserForId(message.authorId!!.toInt())
        val auth = Auth.instance
        if (user != null) {
            showPopupForMessage(user, message, otherMessages)
        } else if (auth != null && auth!!.endUser != null && auth!!.endUser!!.orgId != null) {
            UserManager.instance.getUsers(auth!!.endUser!!.orgId!!, UserManagerCallback {
                val user = UserManager.instance.getUserForId(message.authorId!!.toInt())
                showPopupForMessage(user, message, otherMessages)
            })
        }

    }


    private fun showPopupForMessage(user: User?, message: Message?, otherMessages: Int) {

        try {

            val activity = Drift.currentActivity

            if (message == null || activity == null) {
                return
            }

            if (pw != null) {

                val view = pw!!.contentView
                LoggerHelper.logMessage(TAG, "View Visible: " + view.isShown.toString())
                if (!view.isShown) {
                    LoggerHelper.logMessage(TAG, "Popup activity finished, make a new one")
                } else {
                    //Already showing a new message. Maybe update unread count?
                    LoggerHelper.logMessage(TAG, "Popup still showing, we should update it")

                    val unreadTextView = view.findViewById<TextView>(R.id.drift_sdk_new_message_unread_text_view)
                    updateUnreadCount(unreadTextView, otherMessages)

                    return
                }
            }


            //We need to get the instance of the LayoutInflater, use the context of this activity
            val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //Inflate the view from a predefined XML layout
            val layout = inflater.inflate(R.layout.drift_sdk_new_message_view, null)


            val closeButton = layout.findViewById<Button>(R.id.drift_sdk_new_message_close_button)
            val openButton = layout.findViewById<Button>(R.id.drift_sdk_new_message_open_button)

            val titleTextView = layout.findViewById<TextView>(R.id.drift_sdk_new_message_title_text_view)
            val subtitleTextView = layout.findViewById<TextView>(R.id.drift_sdk_new_message_subtitle_text_view)
            val userImageView = layout.findViewById<ImageView>(R.id.drift_sdk_new_message_user_image_view)

            val unreadTextView = layout.findViewById<TextView>(R.id.drift_sdk_new_message_unread_text_view)

            val bottomLinearLayout = layout.findViewById<LinearLayout>(R.id.drift_sdk_new_message_bottom_linear_layout)

            val backgroundDrawable = DrawableCompat.wrap(bottomLinearLayout.background).mutate()
            DrawableCompat.setTint(backgroundDrawable, ColorHelper.backgroundColor)

            openButton.setTextColor(ColorHelper.foregroundColor)
            closeButton.setTextColor(ColorHelper.foregroundColor)

            closeButton.setOnClickListener {
                MessageReadHelper.markMessageAsReadAlongWithPrevious(message)
                closePopupView()
            }


            openButton.setOnClickListener {
                val conversationId = message.conversationId!!
                val intent = ConversationActivity.intentForConversation(activity, conversationId)
                activity!!.startActivity(intent)
                closePopupView()
            }

            UserPopulationHelper.populateTextAndImageFromUser(activity, user, titleTextView, userImageView)

            val text = message.formattedString

            if (text == null || text.isEmpty() || text.trim { it <= ' ' }.isEmpty()) {
                subtitleTextView.text = String(Character.toChars(0x1F4CE)) + " [Attachment]"
            } else {
                subtitleTextView.text = Html.fromHtml(text)
            }

            updateUnreadCount(unreadTextView, otherMessages)


            val displayMetrics = activity!!.getResources().getDisplayMetrics()

            var width = displayMetrics.widthPixels

            width = Math.min(width, (500 * displayMetrics.density).toInt())

            pw = PopupWindow(layout, width, LinearLayout.LayoutParams.WRAP_CONTENT, false)
            pw!!.animationStyle = R.style.Animation

            pw!!.showAtLocation(layout, Gravity.BOTTOM, 0, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            closePopupView()
        }

    }

    private fun updateUnreadCount(unreadCountTetView: TextView, unreadMessages: Int) {
        if (unreadMessages > 0) {
            if (unreadMessages > 9) {
                unreadCountTetView.text = "+9"
            } else {
                unreadCountTetView.text = unreadMessages.toString()
            }
            unreadCountTetView.visibility = View.VISIBLE
        } else {
            unreadCountTetView.visibility = View.INVISIBLE
        }
    }

    fun closePopupView() {
        if (pw != null) {
            pw!!.dismiss()
            pw = null
        }
    }

    companion object {

        private val TAG = PresentationManager::class.java.simpleName

        val instance = PresentationManager()
    }
}
