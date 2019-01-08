package drift.com.drift.wrappers

import java.util.ArrayList
import java.util.HashMap

import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.model.Attachment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 25/08/2017.
 */

object AttachmentWrapper {

    private val TAG = AttachmentWrapper::class.java.simpleName

    fun getAttachments(attachmentIds: List<Int>, callback: (response: ArrayList<Attachment>?) -> Unit) {

        val imgixOptions = HashMap<String, Any>()

        imgixOptions["img_auto"] = "compress"


        APIManager.conversationClient.getAttachments(attachmentIds, imgixOptions).enqueue(object : Callback<ArrayList<Attachment>> {
            override fun onResponse(call: Call<ArrayList<Attachment>>, response: Response<ArrayList<Attachment>>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<Attachment>>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

}