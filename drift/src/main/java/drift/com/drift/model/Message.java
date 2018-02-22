package drift.com.drift.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import drift.com.drift.helpers.TextHelper;

/**
 * Created by eoin on 28/07/2017.
 */

public class Message {

    public enum SendStatus {
        SENDING,
        SENT,
        FAILED
    }

    @SerializedName("id")
    public Integer id;
    @SerializedName("inboxId")
    public Integer inboxId;
    @SerializedName("type")
    public String type;
    @SerializedName("orgId")
    public Integer orgId;
    @SerializedName("conversationId")
    public Integer conversationId;
    @SerializedName("authorType")
    public String authorType;
    @SerializedName("subject")
    public String subject;
    @SerializedName("body")
    public String body;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("originalHtml")
    public String originalHtml;
    @SerializedName("originalText")
    public String originalText;
    @SerializedName("conversationEvent")
    public String conversationEvent;

    @SerializedName("contentType")
    public String contentType;

    @SerializedName("authorId")
    public int authorId = 0;

    @SerializedName("createdAt")
    public Date createdAt;

    @SerializedName("attachments")
    public ArrayList<Integer> attachmentIds;

    @SerializedName("attributes")
    public MessageAttributes attributes;

    String formattedString;

    public boolean preMessage = false;

    public boolean fakeMessage = false;

    public SendStatus sendStatus = SendStatus.SENT;

    public Boolean isMessageFromEndUser() {

        Auth auth =  Auth.getInstance();
        if (auth != null && auth.endUser != null) {
            return auth.endUser.id.equals(authorId);
        }
        return false;
    }

    public String getFormattedString() {
        if (formattedString == null) {
            formattedString = TextHelper.cleanString(body);
        }

        return formattedString;
    }

    public String readableSendStatus(){
        switch (sendStatus){
            case SENDING:
                return "Sending...";
            case FAILED:
                return "Failed to send";
            case SENT:
                return "";
        }
        return "";
    }

}
