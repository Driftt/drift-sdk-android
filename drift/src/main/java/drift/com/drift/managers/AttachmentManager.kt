package drift.com.drift.managers

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.model.Attachment
import drift.com.drift.wrappers.APICallbackWrapper
import drift.com.drift.wrappers.AttachmentCallback
import drift.com.drift.wrappers.AttachmentWrapper

/**
 * Created by eoin on 25/08/2017.
 */

class AttachmentManager {

    private var attachmentMap = HashMap<Int, Attachment>()

    private var inflightAttachmentRequests = ArrayList<Int>()

    private var attachmentCallback: AttachmentCallback? = null

    fun clearCache() {
        attachmentMap = HashMap()
        inflightAttachmentRequests = ArrayList()
    }

    fun setAttachmentLoadHandle(attachmentLoadHandle: AttachmentCallback) {
        attachmentCallback = attachmentLoadHandle
    }

    fun removeAttachmentLoadHandle() {
        attachmentCallback = null
    }

    fun getAttachment(attachmentId: Int?): Attachment? {

        val attachment = attachmentMap[attachmentId]

        if (attachment == null) {   //Not loaded yet

            if (!inflightAttachmentRequests.contains(attachmentId)) {   //If we havn't asked for attachment yet
                loadAttachments(listOf<Int>(attachmentId))
            }
        }
        return attachment
    }

    fun loadAttachments(attachmentIds: List<Int>) {

        if (inflightAttachmentRequests.containsAll(attachmentIds)) {
            //Already asking for attachments
            LoggerHelper.logMessage(TAG, "Skipping loading attachments, already asking for them")
            return
        }


        AttachmentWrapper.getAttachments(attachmentIds) { response ->
            if (response != null && !response.isEmpty()) {
                //Add to cache
                for (attachment in response) {
                    attachmentMap[attachment.id] = attachment
                }
                //Call callback
            } else {
                //Remove in flight so we can retry
                inflightAttachmentRequests.removeAll(attachmentIds)
            }

            if (attachmentCallback != null) {
                attachmentCallback!!.didLoadAttachments(response)
            }
        }
    }

    companion object {

        private val TAG = AttachmentManager::class.java.simpleName

        val instance = AttachmentManager()
    }
}
