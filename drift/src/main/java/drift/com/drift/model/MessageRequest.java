package drift.com.drift.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import drift.com.drift.helpers.TextHelper;

/**
 * Created by eoin on 15/08/2017.
 */


public class MessageRequest {

    @Expose
    @SerializedName("body")
    public String body;
    @Expose
    @SerializedName("type")
    public String type = "CHAT";
    @Expose
    @SerializedName("authorId")
    private Long authorId;
    @Expose
    @SerializedName("attachments")
    private List<Integer> attachments;
    @Expose
    @SerializedName("context")
    private ConversationContext conversationContext;
    @Expose
    @SerializedName("attributes")
    MessageRequestAttributes attributes;


    public MessageRequest(GoogleMeeting googleMeeting, UserAvailability userAvailability, Long meetingUserId, int conversationId, Date timeSlot){
        this.body = "";
        this.type = "CHAT";
        attributes = new MessageRequestAttributes(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot);
    }

    public MessageRequest(String body, Long authorId, @Nullable Integer attachmentId, Context context){

        this.body = TextHelper.wrapTextForSending(body);
        this.authorId = authorId;

        if (attachmentId != null){
            this.attachments = new ArrayList<>();
            this.attachments.add(attachmentId);
        }

        conversationContext = new ConversationContext(context);

    }

    public Message messageFromRequest(Integer conversationId){

        Message message = new Message();
        message.conversationId = conversationId;
        message.body = TextHelper.cleanString(body);
        message.authorId = authorId;
        message.uuid = UUID.randomUUID().toString();
        message.createdAt = new Date();
        message.sendStatus = Message.SendStatus.SENDING;
        return message;
    }
}