package drift.com.drift.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import drift.com.drift.model.Auth;
import drift.com.drift.model.GoogleMeeting;
import drift.com.drift.model.User;
import drift.com.drift.model.UserAvailability;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APICustomerAPIBuilder {

    @POST("oauth/token")
    @FormUrlEncoded
    Call<Auth> getAuth(@FieldMap HashMap<String, Object> loginPayload);

    @GET("organizations/{orgId}/users")
    Call<ArrayList<User>> getUsers(@Path("orgId") Integer orgId);

    @GET("scheduling/{userId}/availability")
    Call<UserAvailability> getUserAvailability(@Path("userId") int userId);

    @POST("scheduling/{userId}/schedule")
    Call<GoogleMeeting> scheduleMeeting(@Path("userId") int userId, @Query("conversationId") int conversationId, @Body RequestBody timestamp);
}
