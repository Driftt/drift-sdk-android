package drift.com.drift.api


import java.util.HashMap

import drift.com.drift.model.Auth
import drift.com.drift.model.GoogleMeeting
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
}
