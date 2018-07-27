package drift.com.drift.helpers;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;

import drift.com.drift.model.Auth;

public class GlideHelper {

    public static GlideUrl getAttachmentURLForGlide(String attachmentURL) {
        Auth auth = Auth.getInstance();
        String authHeader = "";
        if (auth != null){
            authHeader = "bearer " + auth.getAccessToken();
        }
        Headers headers = new LazyHeaders.Builder()
                .addHeader("Authorization", authHeader).build();
        return new GlideUrl(attachmentURL, headers);
    }

}
