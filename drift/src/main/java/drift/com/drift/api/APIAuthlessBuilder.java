package drift.com.drift.api;

import java.util.HashMap;

import drift.com.drift.model.Embed;
import drift.com.drift.model.IdentifyResponse;
import drift.com.drift.model.SocketAuth;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface APIAuthlessBuilder {

    @GET("https://js.drift.com/embeds/{refreshString}/{embedId}.json")
    Call<Embed> getEmbed(@Path("embedId") String embedId, @Path("refreshString") String refreshRate);

    @POST("https://event.api.drift.com/identify")
    Call<IdentifyResponse> postIdentify(@Body HashMap<String, Object> json);

    @POST("https://{orgId}-{shardId}.chat.api.drift.com/api/auth")
    Call<SocketAuth> postSocketAuth(@Path("orgId") int orgId, @Path("shardId") int shardId, @Body HashMap<String, Object> json);

}
