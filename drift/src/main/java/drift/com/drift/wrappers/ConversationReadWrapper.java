package drift.com.drift.wrappers;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 25/08/2017.
 */

public class ConversationReadWrapper {

    private static String TAG = ConversationReadWrapper.class.getSimpleName();

    public static void markMessageAsRead(int messageId, final APICallbackWrapper<Boolean> callback){

        APIManager.getConversationClient().markMessageAsRead(messageId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 204) {
                    callback.onResponse(true);
                }else{
                    callback.onResponse(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(false);
            }
        });
    }

    public static void markMessageAsReadAlongWithPrevious(int messageId, final APICallbackWrapper<Boolean> callback){

        APIManager.getConversationClient().markMessageAsReadAlongWithPreviousMessages(messageId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 204) {
                    callback.onResponse(true);
                }else{
                    callback.onResponse(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(false);
            }
        });
    }


}
