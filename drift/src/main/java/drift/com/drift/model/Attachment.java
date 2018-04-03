package drift.com.drift.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by eoin on 25/08/2017.
 */

public class Attachment {

    @Expose
    @SerializedName("id")
    public Integer id;

    @Expose
    @SerializedName("conversationId")
    public Integer conversationId;

    @Expose
    @SerializedName("fileName")
    public String fileName;

    @Expose
    @SerializedName("mimeType")
    public String mimeType;

    @Expose
    @SerializedName("publicPreviewUrl")
    public String publicPreviewUrl;

    @Expose
    @SerializedName("size")
    public Integer size;

    @Expose
    @SerializedName("createdAt")
    public Date createdAt;

    @Expose
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
