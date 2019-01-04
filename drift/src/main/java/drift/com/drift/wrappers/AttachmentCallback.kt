package drift.com.drift.wrappers

import java.util.ArrayList

import drift.com.drift.model.Attachment

/**
 * Created by eoin on 26/08/2017.
 */

interface AttachmentCallback {
    fun didLoadAttachments(attachments: ArrayList<Attachment>)
}
