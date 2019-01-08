package drift.com.drift.wrappers

import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 25/08/2017.
 */

object ConversationReadWrapper {

    private val TAG = ConversationReadWrapper::class.java.simpleName

    fun markMessageAsReadAlongWithPrevious(messageId: Int, callback: (response: Boolean) -> Unit) {

        APIManager.conversationClient.markMessageAsReadAlongWithPreviousMessages(messageId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 204) {
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(false)
            }
        })
    }


}
