package drift.com.drift.wrappers;


import java.util.ArrayList;


import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;

import drift.com.drift.model.ConversationExtra;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 04/08/2017.
 */

public class ConversationListWrapper {

    private static String TAG = ConversationListWrapper.class.getSimpleName();

    public static void getConversationsForEndUser(int endUserId, final APICallbackWrapper<ArrayList<ConversationExtra>> callback){

        APIManager.getConversationClient().getConversationsForEndUser (endUserId).enqueue(new Callback<ArrayList<ConversationExtra>>() {
            @Override
            public void onResponse(Call<ArrayList<ConversationExtra>> call, Response<ArrayList<ConversationExtra>> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ConversationExtra>> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

}
