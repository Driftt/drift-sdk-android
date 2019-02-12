package drift.com.drift.wrappers;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.GoogleMeeting;
import drift.com.drift.model.UserAvailability;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 27/02/2018.
 */

public class ScheduleMeetingWrapper {

    private static String TAG = ScheduleMeetingWrapper.class.getSimpleName();

    public static void getUserAvailability(Long userId, final APICallbackWrapper<UserAvailability> callback){

        APIManager.getCustomerClient().getUserAvailability(userId).enqueue(new Callback<UserAvailability>() {
            @Override
            public void onResponse(Call<UserAvailability> call, Response<UserAvailability> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserAvailability> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

    public static void scheduleMeeting(Long userId, int conversationId, double timestamp, final APICallbackWrapper<GoogleMeeting> callback){

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json"), String.valueOf(timestamp));

        APIManager.getCustomerClient().scheduleMeeting(userId, conversationId, body).enqueue(new Callback<GoogleMeeting>() {
            @Override
            public void onResponse(Call<GoogleMeeting> call, Response<GoogleMeeting> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<GoogleMeeting> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

}
