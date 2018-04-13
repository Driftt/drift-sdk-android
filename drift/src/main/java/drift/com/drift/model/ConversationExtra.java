package drift.com.drift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eoin on 04/08/2017.
 */

public class ConversationExtra {

    @Expose
    @SerializedName("conversation")
    public Conversation conversation;

    @Expose
    @SerializedName("unreadMessages")
    public Integer unreadMessages;

    @Expose
    @SerializedName("lastMessage")
    public Message lastMessage;

    @Expose
    @SerializedName("lastAgentMessage")
    public Message lastAgentMessage;

}
