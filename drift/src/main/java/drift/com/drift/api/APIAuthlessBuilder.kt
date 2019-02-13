package drift.com.drift.api

import java.util.HashMap

import drift.com.drift.model.Embed
import drift.com.drift.model.IdentifyResponse
import drift.com.drift.model.SocketAuth
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


internal interface APIAuthlessBuilder {

    @GET("https://js.drift.com/embeds/{refreshString}/{embedId}.json")
    fun getEmbed(@Path("embedId") embedId: String, @Path("refreshString") refreshRate: String): Call<Embed>

    @POST("https://event.api.drift.com/identify")
    fun postIdentify(@Body json: HashMap<String, Any>): Call<IdentifyResponse>

    @POST("https://{orgId}-{shardId}.chat.api.drift.com/api/auth")
    fun postSocketAuth(@Path("orgId") orgId: Int, @Path("shardId") shardId: Int, @Body json: HashMap<String, Any>): Call<SocketAuth>

}
