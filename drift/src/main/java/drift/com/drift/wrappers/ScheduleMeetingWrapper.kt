package drift.com.drift.wrappers

import drift.com.drift.api.APIManager
import drift.com.drift.helpers.LoggerHelper
import drift.com.drift.model.GoogleMeeting
import drift.com.drift.model.UserAvailability
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by eoin on 27/02/2018.
 */

internal object ScheduleMeetingWrapper {

    private val TAG = ScheduleMeetingWrapper::class.java.simpleName

    fun getUserAvailability(userId: Long, callback: (response: UserAvailability?) -> Unit) {

        APIManager.customerClient.getUserAvailability(userId).enqueue(object : Callback<UserAvailability> {
            override fun onResponse(call: Call<UserAvailability>, response: Response<UserAvailability>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<UserAvailability>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

    fun scheduleMeeting(userId: Long, conversationId: Long, timestamp: Double, callback: (response: GoogleMeeting?) -> Unit) {

        val body = RequestBody.create(MediaType.parse("application/json"), timestamp.toString())

        APIManager.customerClient.scheduleMeeting(userId, conversationId, body).enqueue(object : Callback<GoogleMeeting> {
            override fun onResponse(call: Call<GoogleMeeting>, response: Response<GoogleMeeting>) {
                if (response.code() != 200 && response.code() != 201 || response.body() == null) {
                    callback(null)
                } else {
                    callback(response.body())
                }
            }

            override fun onFailure(call: Call<GoogleMeeting>, t: Throwable) {
                LoggerHelper.logMessage(TAG, t.localizedMessage)
                callback(null)
            }
        })
    }

}
