package drift.com.drift.wrappers;

import java.util.ArrayList;

import drift.com.drift.model.Attachment;

/**
 * Created by eoin on 26/08/2017.
 */

public interface AttachmentCallback {
    void didLoadAttachments(ArrayList<Attachment> attachments);
}
