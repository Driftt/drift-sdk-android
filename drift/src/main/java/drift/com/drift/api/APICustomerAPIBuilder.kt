package drift.com.drift.api


import java.util.ArrayList
import java.util.HashMap

import drift.com.drift.model.Auth
import drift.com.drift.model.GoogleMeeting
import drift.com.drift.model.User
import drift.com.drift.model.UserAvailability
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


internal interface APICustomerAPIBuilder {

    @POST("oauth/token")
    @FormUrlEncoded
    fun getAuth(@FieldMap loginPayload: HashMap<String, Any>): Call<Auth>

    @GET("organizations/{orgId}/users")
    fun getUsers(@Path("orgId") orgId: Int?): Call<ArrayList<User>>

    @GET("scheduling/{userId}/availability")
    fun getUserAvailability(@Path("userId") userId: Long): Call<UserAvailability>

    @POST("scheduling/{userId}/schedule")
    fun scheduleMeeting(@Path("userId") userId: Long, @Query("conversationId") conversationId: Long, @Body timestamp: RequestBody): Call<GoogleMeeting>
}
