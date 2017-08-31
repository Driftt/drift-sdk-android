package drift.com.drift.helpers;


import drift.com.drift.model.Message;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.ConversationReadWrapper;


public class MessageReadHelper {

    public static String TAG = MessageReadHelper.class.getSimpleName();

    public static void markMessageAsRead(final Message message) {

        if (message == null) {
            return;
        }

        ConversationReadWrapper.markMessageAsRead(message.id, new APICallbackWrapper<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (!response) {
                    LoggerHelper.logMessage(TAG, "Failed to mark conversation as read: " + message.id);
                }
            }
        });
    }

    public static void markMessageAsReadAlongWithPrevious(final Message message) {

        if (message == null || message.id == null) {
            return;
        }

        ConversationReadWrapper.markMessageAsReadAlongWithPrevious(message.id, new APICallbackWrapper<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (!response) {
                    LoggerHelper.logMessage(TAG, "Failed to mark conversation as read: " + message.id);
                }
            }
        });
    }

}
