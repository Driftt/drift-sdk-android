package drift.com.drift.managers;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageRequest;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.MessagesWrapper;

/**
 * Created by eoin on 11/08/2017.
 */

public class MessageManager {

    private static String TAG = MessageManager.class.getSimpleName();

    private static MessageManager _messageManager = new MessageManager();

    public static MessageManager getInstance() {
        return _messageManager;
    }

    private HashMap<Integer, ArrayList<Message>> messageCache = new HashMap<>();
    private HashMap<Integer, ArrayList<Message>> failedMessageCache = new HashMap<>();

    public void clearCache(){
        messageCache = new HashMap<>();
        failedMessageCache = new HashMap<>();
    }

    @Nullable
    public ArrayList<Message> getMessagesForConversationId(int conversationId) {

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages == null || failedMessages.isEmpty()) {
            return messageCache.get(conversationId);
        }

        ArrayList<Message> successMessages = messageCache.get(conversationId);

        if (successMessages == null || successMessages.isEmpty()) {
            successMessages = new ArrayList<>();
        }

        ArrayList<Message> returnedArr = new ArrayList<>();

        returnedArr.addAll(successMessages);
        returnedArr.addAll(failedMessages);


        Collections.sort(returnedArr, new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                if (o1.createdAt == null || o2.createdAt == null)
                    return 0;
                return o2.createdAt.compareTo(o1.createdAt);
            }
        });

        return returnedArr;
    }

    public void addMessageFailedToConversation(Message message, int conversationId){

        ArrayList<Message> successMessages = messageCache.get(conversationId);

        if (successMessages != null && successMessages.contains(message)) {
            successMessages.remove(message);
        }

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages != null && !failedMessages.contains(message)){
            failedMessages.add(message);
            return;
        }

        failedMessages = new ArrayList<>();
        failedMessages.add(message);

        failedMessageCache.put(conversationId, failedMessages);
    }


    public void removeMessageFromFailedCache(Message message, int conversationId) {

        ArrayList<Message> failedMessages = failedMessageCache.get(conversationId);

        if (failedMessages != null && failedMessages.contains(message)){
            failedMessages.remove(message);
        }
    }

    public void getMessagesForConversation(final int conversationId, final APICallbackWrapper<ArrayList<Message>> conversationsCallback) {

        MessagesWrapper.getMessagesForConversationId(conversationId, new APICallbackWrapper<ArrayList<Message>>() {
            @Override
            public void onResponse(ArrayList<Message> response) {

                if (response != null) {

                    Collections.reverse(response);

                    messageCache.put(conversationId, response);
                }

                conversationsCallback.onResponse(getMessagesForConversationId(conversationId));

            }
        });
    }

    public void sendMessageForConversationId(int conversationId, MessageRequest messageRequest,  final APICallbackWrapper<Message> conversationsCallback) {


        MessagesWrapper.sendMessageToConversation(conversationId, messageRequest, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                conversationsCallback.onResponse(response);
            }
        });

    }

    public void createConversation(String body, final APICallbackWrapper<Message> callbackWrapper) {

        MessagesWrapper.createConversation(body, new APICallbackWrapper<Message>() {
            @Override
            public void onResponse(Message response) {

                callbackWrapper.onResponse(response);
            }
        });

    }
}
