package drift.com.drift.api;


import java.util.ArrayList;
import java.util.HashMap;
import drift.com.drift.model.Auth;
import drift.com.drift.model.User;
import drift.com.drift.model.UserAvailability;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;



public interface APICustomerAPIBuilder {

    @POST("oauth/token")
    @FormUrlEncoded
    Call<Auth> getAuth(@FieldMap HashMap<String, Object> loginPayload);

    @GET("organizations/{orgId}/users")
    Call<ArrayList<User>> getUsers(@Path("orgId") Integer orgId);


    @GET("scheduling/{userId}/availability")
    Call<UserAvailability> getUserAvailability(@Path("userId") int userId);
}
