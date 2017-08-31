package drift.com.drift.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 25/08/2017.
 */

public class Attachment {

    @SerializedName("id")
    public Integer id;

    @SerializedName("conversationId")
    public Integer conversationId;

    @SerializedName("fileName")
    public String fileName;

    @SerializedName("mimeType")
    public String mimeType;

    @SerializedName("publicPreviewUrl")
    public String publicPreviewUrl;

    @SerializedName("size")
    public Integer size;

    @SerializedName("createdAt")
    public Date createdAt;

    @SerializedName("publicId")
    String publicId;

    public boolean isImage(){
        return mimeType.equalsIgnoreCase("image/jpeg") || mimeType.equalsIgnoreCase("image/png") || mimeType.equalsIgnoreCase("image/gif") || mimeType.equalsIgnoreCase("image/jpg");
    }

    public String generateDownloadURL(){
        return "https://conversation.api.driftt.com/attachments/public/" + publicId +"/data";
    }

    public Uri getURL() {
        if (isImage()) {
            if (publicPreviewUrl != null) {
                return Uri.parse(publicPreviewUrl);
            } else {
                return Uri.parse(generateDownloadURL());
            }
        }else{
            return Uri.parse(generateDownloadURL());
        }
    }


}
