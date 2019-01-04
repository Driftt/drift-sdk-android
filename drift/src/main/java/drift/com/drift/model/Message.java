package drift.com.drift.model;

import com.google.gson.annotations.Expose;
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

    @Expose
    @SerializedName("id")
    public Integer id;
    @Expose
    @SerializedName("inboxId")
    public Integer inboxId;
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("orgId")
    public Integer orgId;
    @Expose
    @SerializedName("conversationId")
    public Integer conversationId;
    @Expose
    @SerializedName("authorType")
    public String authorType;
    @Expose
    @SerializedName("subject")
    public String subject;
    @Expose
    @SerializedName("body")
    public String body;
    @Expose
    @SerializedName("uuid")
    public String uuid;
    @Expose
    @SerializedName("originalHtml")
    public String originalHtml;
    @Expose
    @SerializedName("originalText")
    public String originalText;
    @Expose
    @SerializedName("conversationEvent")
    public String conversationEvent;
    @Expose
    @SerializedName("contentType")
    public String contentType;
    @Expose
    @SerializedName("authorId")
    public Long authorId = 0L;
    @Expose
    @SerializedName("createdAt")
    public Date createdAt;
    @Expose
    @SerializedName("attachments")
    public ArrayList<Integer> attachmentIds;
    @Expose
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
