package drift.com.drift.managers;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Attachment;
import drift.com.drift.wrappers.APICallbackWrapper;
import drift.com.drift.wrappers.AttachmentCallback;
import drift.com.drift.wrappers.AttachmentWrapper;

/**
 * Created by eoin on 25/08/2017.
 */

public class AttachmentManager {

    private static String TAG = AttachmentManager.class.getSimpleName();

    private static AttachmentManager _attachmentManager = new AttachmentManager();

    public static AttachmentManager getInstance() {
        return _attachmentManager;
    }

    private HashMap<Integer, Attachment> attachmentMap = new HashMap<>();

    private ArrayList<Integer> inflightAttachmentRequests = new ArrayList<>();

    @Nullable
    private AttachmentCallback attachmentCallback = null;

    public void clearCache(){
        attachmentMap = new HashMap<>();
        inflightAttachmentRequests = new ArrayList<>();
    }

    public void setAttachmentLoadHandle(AttachmentCallback attachmentLoadHandle) {
        attachmentCallback = attachmentLoadHandle;
    }

    public void removeAttachmentLoadHandle() {
        attachmentCallback = null;
    }

    public Attachment getAttachment(Integer attachmentId) {

        Attachment attachment = attachmentMap.get(attachmentId);

        if (attachment == null) {   //Not loaded yet

            if (!inflightAttachmentRequests.contains(attachmentId)) {   //If we havn't asked for attachment yet
                loadAttachments(Collections.singletonList(attachmentId));
            }
        }
        return attachment;
    }

    public void loadAttachments(final List<Integer> attachmentIds){

        if (inflightAttachmentRequests.containsAll(attachmentIds)) {
            //Already asking for attachments
            LoggerHelper.logMessage(TAG, "Skipping loading attachments, already asking for them");
            return;
        }


        AttachmentWrapper.getAttachments(attachmentIds, new APICallbackWrapper<ArrayList<Attachment>>() {
            @Override
            public void onResponse(ArrayList<Attachment> response) {

                if (response != null && !response.isEmpty()) {
                    //Add to cache
                    for (Attachment attachment : response) {
                        attachmentMap.put(attachment.id, attachment);
                    }
                    //Call callback
                } else {
                    //Remove in flight so we can retry
                    inflightAttachmentRequests.removeAll(attachmentIds);
                }

                if (attachmentCallback != null) {
                    attachmentCallback.didLoadAttachments(response);
                }
            }
        });
    }
}
