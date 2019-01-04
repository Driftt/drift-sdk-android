package drift.com.drift.managers;

import java.util.ArrayList;
import drift.com.drift.model.ConversationExtra;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.ConversationListWrapper;

/**
 * Created by eoin on 08/08/2017.
 */

public class ConversationManager {

    private static String TAG = ConversationManager.class.getSimpleName();

    private static ConversationManager _conversationManager = new ConversationManager();

    public static ConversationManager getInstance() {
        return _conversationManager;
    }

    private ArrayList<ConversationExtra> conversations = new ArrayList<>();

    private int manuallyAddedUnreadMessages = 0;

    private boolean apiCallComplete = false;

    public ArrayList<ConversationExtra> getConversations() {
        return conversations;
    }

    public void manuallyAddUnreadCount(){
        manuallyAddedUnreadMessages += 1;
    }

    public boolean isApiCallComplete() {
        return apiCallComplete;
    }

    public int getUnreadCountForUser() {

        int unreadCount = manuallyAddedUnreadMessages;

        for (ConversationExtra conversationExtra : conversations) {
            if (conversationExtra.unreadMessages != 0) {
                unreadCount += conversationExtra.unreadMessages;
            }
        }

        return unreadCount;
    }

    public void clearCache(){
        conversations = new ArrayList<>();
        apiCallComplete = false;
    }

    public void getConversationsForEndUser(Long endUserId, final APICallbackWrapper<ArrayList<ConversationExtra>> conversationsCallback) {

        ConversationListWrapper.getConversationsForEndUser(endUserId, new APICallbackWrapper<ArrayList<ConversationExtra>>() {
            @Override
            public void onResponse(ArrayList<ConversationExtra> response) {
                apiCallComplete = true;
                if (response != null) {
                    manuallyAddedUnreadMessages = 0;

                    ArrayList<ConversationExtra> filteredConversationExtras = new ArrayList<>();

                    for (ConversationExtra conversationExtra : response) {
                        if (conversationExtra.conversation != null && !conversationExtra.conversation.type.equals("EMAIL")){
                            filteredConversationExtras.add(conversationExtra);
                        }
                    }
                    conversations = filteredConversationExtras;
                }

                conversationsCallback.onResponse(response);
            }
        });

    }



}
