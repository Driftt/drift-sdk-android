package drift.com.drift.api

import drift.com.drift.model.GoogleMeeting
import drift.com.drift.model.UserAvailability
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

internal interface APIMeetingAPIBuilder {

    @GET("scheduling/{userId}/availability")
    fun getUserAvailability(@Path("userId") userId: Long): Call<UserAvailability>

    @POST("scheduling/{userId}/schedule")
    fun scheduleMeeting(@Path("userId") userId: Long, @Query("conversationId") conversationId: Long, @Body timestamp: RequestBody): Call<GoogleMeeting>
}