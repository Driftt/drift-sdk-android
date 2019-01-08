package drift.com.drift.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import drift.com.drift.R
import drift.com.drift.helpers.DateHelper
import drift.com.drift.helpers.UserPopulationHelper
import drift.com.drift.managers.UserManager
import drift.com.drift.model.Conversation
import drift.com.drift.model.ConversationExtra


internal class ConversationListAdapter(private val context: Context, private var conversationExtraList: List<ConversationExtra>?) : RecyclerView.Adapter<ConversationListAdapter.ConversationListCell>() {

    fun updateDate(conversationExtras: ArrayList<ConversationExtra>) {
        conversationExtraList = conversationExtras
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationListCell {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.drift_sdk_conversation_cell, parent, false)
        return ConversationListCell(itemView)
    }

    override fun onBindViewHolder(holder: ConversationListCell, position: Int) {
        holder.setupForConversation(conversationExtraList!![position])
    }

    override fun getItemCount(): Int {
        return conversationExtraList!!.size
    }

    fun getItemAt(position: Int): Conversation? {
        return conversationExtraList!![position].conversation
    }

    internal inner class ConversationListCell(view: View) : RecyclerView.ViewHolder(view) {

        private var userNameTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_cell_user_name_text_view)
        private var conversationTimeTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_cell_time_text_view)
        private var conversationPreviewTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_cell_preview_text_view)
        private var userImageView: ImageView = view.findViewById(R.id.drift_sdk_conversation_cell_image_view)
        private var unreadCountTextView: TextView = view.findViewById(R.id.drift_sdk_conversation_cell_unread_text_view)


        fun setupForConversation(conversationExtra: ConversationExtra) {

            if (conversationExtra.conversation == null) {
                return
            }

            val conversation = conversationExtra.conversation

            if (conversation!!.preview == null || conversation.preview!!.isEmpty() || conversation.preview!!.trim { it <= ' ' }.isEmpty()) {
                conversationPreviewTextView.text = String(Character.toChars(0x1F4CE)) + " [Attachment]"
            } else {
                val preview = conversation.preview
                conversationPreviewTextView.text = preview
            }

            if (conversation.updatedAt != null) {

                val updatedAt = conversation.updatedAt

                conversationTimeTextView.text = DateHelper.formatDateForConversation(context, updatedAt)

            } else {
                conversationTimeTextView.text = ""
            }


            if (conversationExtra.lastAgentMessage != null) {
                val user = UserManager.instance.getUserForId(conversationExtra.lastAgentMessage!!.authorId!!.toInt())
                UserPopulationHelper.populateTextAndImageFromUser(context, user, userNameTextView, userImageView)
            } else if (conversationExtra.lastMessage != null
                    && conversationExtra.lastMessage!!.attributes != null
                    && conversationExtra.lastMessage!!.attributes!!.preMessages != null
                    && !conversationExtra.lastMessage!!.attributes!!.preMessages.isEmpty()) {

                val preMessage = conversationExtra.lastMessage!!.attributes!!.preMessages[0]
                val user = UserManager.instance.getUserForId(preMessage.sender!!.id!!)
                UserPopulationHelper.populateTextAndImageFromUser(context, user, userNameTextView, userImageView)
            } else {
                Glide.with(context).clear(userImageView)
                userNameTextView.text = ""
            }


            if (conversationExtra.unreadMessages == 0) {
                unreadCountTextView.visibility = View.GONE
            } else {
                if (conversationExtra.unreadMessages > 100) {
                    unreadCountTextView.setText(R.string.drift_sdk_99_plus)
                } else {
                    unreadCountTextView.text = conversationExtra.unreadMessages.toString()
                }
                unreadCountTextView.visibility = View.VISIBLE
            }

        }

    }

}
