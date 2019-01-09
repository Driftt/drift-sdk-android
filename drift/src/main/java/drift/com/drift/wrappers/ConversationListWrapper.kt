package drift.com.drift.wrappers


import java.util.ArrayList


import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper

import drift.com.drift.model.ConversationExtra

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 04/08/2017.
 */

internal object ConversationListWrapper {

    private val TAG = ConversationListWrapper::class.java.simpleName

    fun getConversationsForEndUser(endUserId: Long?, callback: (response: ArrayList<ConversationExtra>?) -> Unit) {

        APIManager.conversationClient.getConversationsForEndUser(endUserId).enqueue(object : Callback<ArrayList<ConversationExtra>> {
            override fun onResponse(call: Call<ArrayList<ConversationExtra>>, response: Response<ArrayList<ConversationExtra>>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<ConversationExtra>>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

}
