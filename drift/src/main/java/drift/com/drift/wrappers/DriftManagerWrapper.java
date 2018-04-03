package drift.com.drift.wrappers;

import java.util.HashMap;

import drift.com.drift.api.APIManager;
import drift.com.drift.model.Auth;
import drift.com.drift.model.Embed;
import drift.com.drift.model.IdentifyResponse;
import drift.com.drift.model.SocketAuth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 28/07/2017.
 */

public class DriftManagerWrapper {

    private static String TAG = DriftManagerWrapper.class.getSimpleName();

    public static void getEmbed(String embedId, final APICallbackWrapper<Embed> callback){

        APIManager.getAuthlessClient().getEmbed(embedId, "30000").enqueue(new Callback<Embed>() {
            @Override
            public void onResponse(Call<Embed> call, Response<Embed> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<Embed> call, Throwable t) {
                callback.onResponse(null);
            }
        });
    }


    public static void postIdentity(int orgId, String userId, String email, final APICallbackWrapper<IdentifyResponse> callback){

        HashMap<String, Object> jsonPayload = new HashMap<>();

        jsonPayload.put("orgId", orgId);
        jsonPayload.put("userId", userId);

        HashMap<String, Object> inlineEmailAttributes = new HashMap<>();
        inlineEmailAttributes.put("email", email);
        jsonPayload.put("attributes", inlineEmailAttributes);


        APIManager.getAuthlessClient().postIdentify(jsonPayload).enqueue(new Callback<IdentifyResponse>() {
            @Override
            public void onResponse(Call<IdentifyResponse> call, Response<IdentifyResponse> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<IdentifyResponse> call, Throwable t) {
                callback.onResponse(null);
            }
        });
    }

    public static void getAuth(int orgId, String userId, String email, String redirectUri, String clientId, final APICallbackWrapper<Auth> callback){

        HashMap<String, Object> jsonPayload = new HashMap<>();

        jsonPayload.put("email", email);
        jsonPayload.put("org_id", orgId);
        jsonPayload.put("user_id", userId);
        jsonPayload.put("grant_type", "sdk");
        jsonPayload.put("redirect_uri", redirectUri);
        jsonPayload.put("client_id", clientId);


        APIManager.getCustomerClient().getAuth(jsonPayload).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                callback.onResponse(null);
            }
        });
    }

    public static void getSocketAuth(String accessToken, final APICallbackWrapper<SocketAuth> callback){

        HashMap<String, Object> jsonPayload = new HashMap<>();

        jsonPayload.put("access_token", accessToken);

        APIManager.getAuthlessClient().postSocketAuth(jsonPayload).enqueue(new Callback<SocketAuth>() {
            @Override
            public void onResponse(Call<SocketAuth> call, Response<SocketAuth> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<SocketAuth> call, Throwable t) {
                callback.onResponse(null);
            }
        });
    }

}
