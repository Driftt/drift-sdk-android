package drift.com.drift.adapters

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.content.res.AppCompatResources
import android.text.Html
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView


import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import drift.com.drift.R
import drift.com.drift.activities.ConversationActivity
import drift.com.drift.activities.ImageViewerActivity
import drift.com.drift.helpers.ColorHelper
import drift.com.drift.helpers.DateHelper
import drift.com.drift.helpers.GlideHelper
import drift.com.drift.managers.AttachmentManager
import drift.com.drift.managers.UserManager
import drift.com.drift.model.Attachment
import drift.com.drift.model.Auth
import drift.com.drift.model.Message
import drift.com.drift.model.User
import drift.com.drift.views.AttachmentView


class ConversationAdapter(private val activity: ConversationActivity, messages: MutableList<Message>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages: MutableList<Message>? = null

    private object CellTypes {
        const val MESSAGE = 0
        const val MEETING_CELL = 1
    }

    init {
        if (messages == null) {
            this.messages = ArrayList()
        } else {
            this.messages = messages
        }
    }

    fun updateData(messages: ArrayList<Message>?) {
        if (messages == null) {
            return
        }
        this.messages = messages
        notifyDataSetChanged()
    }

    fun updateDataAddingInOneMessage(messages: ArrayList<Message>?, recyclerView: RecyclerView) {
        if (messages == null) {
            return
        }
        this.messages = messages
        notifyItemInserted(0)
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
            if (linearLayoutManager!!.findFirstVisibleItemPosition() < 1) {
                recyclerView.scrollToPosition(0)
            }
        }
    }

    fun addMessage(recyclerView: RecyclerView, message: Message) {

        if (messages == null) {
            messages = ArrayList()
        }
        messages!!.add(0, message)
        notifyItemInserted(0)

        if (recyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
            if (linearLayoutManager!!.findFirstVisibleItemPosition() < 1) {
                recyclerView.scrollToPosition(0)
            }
        }
    }

    fun updateForAttachments(attachments: ArrayList<Attachment>) {
        notifyDataSetChanged()
    }

    fun updateMessage(message: Message) {

        if (messages == null) {
            return
        }

        val index = messages!!.indexOf(message)

        if (index != -1) {

            messages!![index] = message
            notifyItemChanged(index)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = getItemAt(position)

        return if (message.attributes != null && message.attributes!!.appointmentInfo != null) {
            CellTypes.MEETING_CELL
        } else CellTypes.MESSAGE

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            CellTypes.MESSAGE -> return MessageCell(inflater.inflate(R.layout.drift_sdk_conversation_message_cell, parent, false))
            CellTypes.MEETING_CELL -> return MeetingCell(inflater.inflate(R.layout.drift_sdk_conversation_meeting_cell, parent, false))
        }
        return MessageCell(inflater.inflate(R.layout.drift_sdk_conversation_message_cell, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var showDayHeader = true
        val message = getItemAt(position)
        if (position < itemCount - 1) {
            val pastMessage = getItemAt(position + 1)

            if (message.createdAt == null || pastMessage.createdAt == null) {
                Log.d("TAG", "Null Created At")

            } else {
                showDayHeader = !DateHelper.isSameDay(message.createdAt, pastMessage.createdAt)
            }
        }

        if (holder.itemViewType == CellTypes.MESSAGE) {
            val messageCell = holder as MessageCell
            messageCell.setupForMessage(message, showDayHeader)
            messageCell.meetingScheduleButton.setOnClickListener {
                val attributes = message.attributes
                if (attributes?.presentSchedule != null) {
                    activity.didPressScheduleMeetingFor(attributes.presentSchedule!!)
                }
            }
        } else if (holder.itemViewType == CellTypes.MEETING_CELL) {
            val meetingCell = holder as MeetingCell
            meetingCell.setupForMessage(message, showDayHeader)
        }
    }

    override fun getItemCount(): Int {
        return messages!!.size
    }

    fun getItemAt(position: Int): Message {
        return messages!![position]
    }


    internal inner class MessageCell(view: View) : RecyclerView.ViewHolder(view) {

        private var dayHeader: LinearLayout = view.findViewById(R.id.drift_sdk_conversation_message_cell_day_header)
        private var dayHeaderTextView: TextView = dayHeader.findViewById(R.id.drift_sdk_conversation_day_divider_text_view)

        private var userImageView: ImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_user_image_view)
        private var userTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_user_text_view)
        private var timeTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_time_text_view)
        private var mainTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_main_text_view)

        private var singleAttachmentImageView: ImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachment_image_view)
        private var multipleAttachmentsLinearLayout: LinearLayout = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachments_linear_layout)
        private var multipleAttachmentScrollView: HorizontalScrollView = view.findViewById(R.id.drift_sdk_conversation_message_cell_attachments_nested_scroll_view)

        private var contentLinearLayout: LinearLayout = view.findViewById(R.id.drift_sdk_conversation_message_content_linear_layout)

        private var meetingViewRelativeLayout: RelativeLayout = view.findViewById(R.id.drift_sdk_conversation_message_cell_schedule_meeting_relative_layout)
        private var meetingViewUserImageView: ImageView = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_view_user_image_view)
        private var meetingViewTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_view_text_view)
        internal var meetingScheduleButton: Button = view.findViewById(R.id.drift_sdk_conversation_message_cell_meeting_button)


        fun setupForMessage(message: Message, showDayHeader: Boolean) {

            if (showDayHeader) {

                if (DateHelper.isSameDay(message.createdAt, Date())) {
                    dayHeaderTextView.setText(R.string.drift_sdk_today)
                } else {
                    val updatedTime = DateFormat.getDateFormat(activity).format(message.createdAt)
                    dayHeaderTextView.text = updatedTime
                }
                dayHeader.visibility = View.VISIBLE
            } else {
                dayHeader.visibility = View.GONE
            }


            if (message.attachmentIds != null && !message.attachmentIds!!.isEmpty()) {


                val attachments = ArrayList<Attachment>()

                for (attachmentId in message.attachmentIds!!) {
                    val attachment = AttachmentManager.instance.getAttachment(attachmentId)
                    if (attachment != null) {
                        attachments.add(attachment)
                    }
                }


                if (!attachments.isEmpty()) {

                    if (attachments.size == 1) {
                        val attachment = attachments[0]
                        if (attachment.isImage) {

                            multipleAttachmentScrollView.visibility = View.GONE
                            singleAttachmentImageView.visibility = View.VISIBLE


                            val displayMetrics = activity.resources.displayMetrics

                            val requestOptions = RequestOptions()
                                    .centerCrop()
                                    .transform(RoundedCorners((4 * displayMetrics.density).toInt()))
                                    .placeholder(R.drawable.drift_sdk_attachment_placeholder)


                            Glide.with(activity)
                                    .load(GlideHelper.getAttachmentURLForGlide(attachment.url))
                                    .apply(requestOptions)
                                    .into(singleAttachmentImageView)

                            singleAttachmentImageView.setOnClickListener {
                                val intent = ImageViewerActivity.intentForUri(activity, attachment.url)
                                activity.startActivity(intent)
                            }
                        } else {
                            singleAttachmentImageView.visibility = View.GONE
                            multipleAttachmentsLinearLayout.removeAllViews()
                            val attachmentView = AttachmentView(activity)
                            attachmentView.setupForAttachment(attachment, message)
                            multipleAttachmentsLinearLayout.addView(attachmentView)
                            multipleAttachmentScrollView.visibility = View.VISIBLE
                        }
                    } else {
                        singleAttachmentImageView.visibility = View.GONE
                        multipleAttachmentsLinearLayout.removeAllViews()
                        for (attachment in attachments) {
                            val attachmentView = AttachmentView(activity)
                            attachmentView.setupForAttachment(attachment, message)
                            multipleAttachmentsLinearLayout.addView(attachmentView)
                        }
                        multipleAttachmentScrollView.visibility = View.VISIBLE
                    }
                } else {
                    ///Load Attachments
                    singleAttachmentImageView.visibility = View.VISIBLE
                    singleAttachmentImageView.setOnClickListener(null)
                    multipleAttachmentScrollView.visibility = View.GONE
                }

            } else {
                singleAttachmentImageView.visibility = View.GONE
                multipleAttachmentScrollView.visibility = View.GONE
            }


            val timeStamp = DateFormat.getTimeFormat(activity).format(message.createdAt)
            timeTextView.text = timeStamp

            val body = message.formattedString
            if (body == null || body.isEmpty()) {
                mainTextView.visibility = View.GONE
            } else {
                mainTextView.visibility = View.VISIBLE
                mainTextView.text = Html.fromHtml(body)
                mainTextView.setTextColor(ContextCompat.getColor(activity, R.color.drift_sdk_black))
            }
            mainTextView.movementMethod = LinkMovementMethod.getInstance()



            if (message.sendStatus != Message.SendStatus.SENT) {
                timeTextView.text = message.readableSendStatus()
            }

            if (message.attributes != null && message.attributes!!.presentSchedule != null) {

                val user = UserManager.instance.getUserForId(message.attributes!!.presentSchedule!!)

                if (user != null) {


                    val requestOptions = RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.drift_sdk_placeholder)

                    Glide.with(activity)
                            .load(user.avatarUrl)
                            .apply(requestOptions)
                            .into(meetingViewUserImageView)


                    meetingViewTextView.text = activity.getString(R.string.drift_sdk_scheduling_meeting_with, user.userName)
                } else {
                    Glide.with(activity).clear(meetingViewUserImageView)
                    meetingViewTextView.setText(R.string.drift_sdk_scheduling_meeting)
                }

                meetingViewRelativeLayout.visibility = View.VISIBLE

                val backgroundDrawable = DrawableCompat.wrap(meetingScheduleButton.background).mutate()
                DrawableCompat.setTint(backgroundDrawable, ColorHelper.backgroundColor)

                meetingScheduleButton.setTextColor(ColorHelper.foregroundColor)

            } else {
                meetingViewRelativeLayout.visibility = View.GONE
            }


            setupForUser(message, UserManager.instance.getUserForId(message.authorId!!.toInt()))

        }

        private fun setupForUser(message: Message, user: User?) {

            var uriToLoad: Uri? = null
            var ignoreUri = false

            if (message.isMessageFromEndUser!!) {

                val auth = Auth.instance
                if (auth?.endUser != null) {
                    val endUser = auth.endUser

                    if (endUser!!.name != null && !endUser.name!!.isEmpty()) {
                        userTextView.text = endUser.name
                    } else if (endUser.email != null && !endUser.email!!.isEmpty()) {
                        userTextView.text = endUser.email
                    } else {
                        userTextView.setText(R.string.drift_sdk_you)
                    }
                    if (endUser.avatarUrl != null) {
                        uriToLoad = Uri.parse(endUser.avatarUrl)
                    }

                } else {
                    userTextView.setText(R.string.drift_sdk_you)
                }


            } else if (user != null) {
                userTextView.text = user.userName

                if (user.bot!!) {
                    ignoreUri = true
                    val placeholderDrawable = AppCompatResources.getDrawable(activity, R.drawable.drift_sdk_robot)
                    val backgroundDrawable = AppCompatResources.getDrawable(activity, R.drawable.drift_sdk_bot_background)

                    DrawableCompat.setTint(backgroundDrawable!!, ColorHelper.backgroundColor)


                    val layers = arrayOfNulls<Drawable>(2)
                    layers[0] = backgroundDrawable
                    layers[1] = placeholderDrawable
                    val layerDrawable = LayerDrawable(layers)
                    userImageView.setImageDrawable(layerDrawable)


                } else if (user.avatarUrl != null) {
                    uriToLoad = Uri.parse(user.avatarUrl)
                }

            } else {
                //Unknown User
                userTextView.setText(R.string.drift_sdk_unknown_user)
            }

            if (!ignoreUri) {

                val requestOptions = RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.drift_sdk_placeholder)

                Glide.with(activity)
                        .load(uriToLoad)
                        .apply(requestOptions)
                        .into(userImageView)
            }
        }
    }

    internal inner class MeetingCell(view: View) : RecyclerView.ViewHolder(view) {

        private var dayHeader: LinearLayout = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_day_header)
        private var dayHeaderTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_day_divider_text_view)

        private var meetingUserImage: ImageView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_user_image_view)
        private var titleTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_title_text_view)

        private var meetingTimeTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_time_text_view)
        private var meetingDateTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_date_text_view)
        private var meetingTimeZoneTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_meeting_cell_timezone_text_view)


        fun setupForMessage(message: Message, showDayHeader: Boolean) {
            if (showDayHeader) {
                dayHeader.visibility = View.VISIBLE
                if (DateHelper.isSameDay(message.createdAt, Date())) {
                    dayHeaderTextView.setText(R.string.drift_sdk_today)
                } else {
                    val updatedTime = DateFormat.getDateFormat(activity).format(message.createdAt)
                    dayHeaderTextView.text = updatedTime
                }

            } else {
                dayHeader.visibility = View.GONE
            }

            if (message.attributes != null) {
                if (message.attributes!!.appointmentInfo != null && message.attributes!!.appointmentInfo!!.agentId != null) {
                    val appointmentInformation = message.attributes!!.appointmentInfo

                    val user = UserManager.instance.getUserForId(appointmentInformation!!.agentId!!)

                    if (user != null) {
                        val requestOptions = RequestOptions()
                                .circleCrop()
                                .placeholder(R.drawable.drift_sdk_placeholder)
                        Glide.with(activity).load(user.avatarUrl).apply(requestOptions).into(meetingUserImage)

                        titleTextView.text = activity.getString(R.string.drift_sdk_scheduled_meeting_with, user.userName)

                    } else {
                        titleTextView.setText(R.string.drift_sdk_scheduled_meeting)
                        Glide.with(activity).clear(meetingUserImage)
                    }


                    val startDate = appointmentInformation.availabilitySlot
                    val endDate = Date(startDate!!.time + appointmentInformation.slotDuration!! * 60000)

                    val startDateText = DateHelper.formatDateForScheduleTime(activity, startDate)
                    val endDateText = DateHelper.formatDateForScheduleTime(activity, endDate)

                    meetingTimeTextView.text = activity.getString(R.string.drift_sdk_dash_divided_strings, startDateText, endDateText)

                    val dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL)
                    meetingDateTextView.text = dateFormat.format(startDate)
                    val cal = Calendar.getInstance()
                    val tz = cal.timeZone
                    meetingTimeZoneTextView.text = tz.id

                }
            }
        }
    }

}
