package drift.com.drift.helpers


import drift.com.drift.model.Message
import drift.com.drift.wrappers.ConversationReadWrapper


object MessageReadHelper {

    var TAG = MessageReadHelper::class.java.simpleName

    fun markMessageAsReadAlongWithPrevious(message: Message?) {

        val messageId = message?.id ?: return

        ConversationReadWrapper.markMessageAsReadAlongWithPrevious(messageId) { response ->
            if (!response) {
                LoggerHelper.logMessage(TAG, "Failed to mark conversation as read: " + message.id!!)
            }
        }
    }

}
