package drift.com.drift.helpers

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders

import drift.com.drift.model.Auth

internal object GlideHelper {

    fun getAttachmentURLForGlide(attachmentURL: String): GlideUrl {
        val auth = Auth.instance
        var authHeader = ""
        if (auth != null) {
            authHeader = "bearer " + auth.accessToken
        }
        val headers = LazyHeaders.Builder()
                .addHeader("Authorization", authHeader).build()
        return GlideUrl(attachmentURL, headers)
    }

}
