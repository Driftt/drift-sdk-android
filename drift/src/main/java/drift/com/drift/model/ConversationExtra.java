package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 04/08/2017.
 */

public class ConversationExtra {

    @SerializedName("conversation")
    public Conversation conversation;

    @SerializedName("unreadMessages")
    public Integer unreadMessages;

    @SerializedName("lastMessage")
    public Message lastMessage;

    @SerializedName("lastAgentMessage")
    public Message lastAgentMessage;

}
