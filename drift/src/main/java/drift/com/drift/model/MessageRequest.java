package drift.com.drift.model;

import android.content.Context;
import android.support.annotation.Nullable;

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


    @SerializedName("body")
    public String body;
    @SerializedName("type")
    public String type = "CHAT";
    @SerializedName("authorId")
    public int authorId;
    @SerializedName("attachments")
    public List<Integer> attachments;

    @SerializedName("context")
    public ConversationContext conversationContext;

    @SerializedName("attributes")
    MessageRequestAttributes attributes;


    public MessageRequest(GoogleMeeting googleMeeting, UserAvailability userAvailability, int meetingUserId, int conversationId, Date timeSlot){
        this.body = "";
        this.type = "CHAT";
        attributes = new MessageRequestAttributes(googleMeeting, userAvailability, meetingUserId, conversationId, timeSlot);
    }

    public MessageRequest(String body, int authorId, @Nullable Integer attachmentId, Context context){

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