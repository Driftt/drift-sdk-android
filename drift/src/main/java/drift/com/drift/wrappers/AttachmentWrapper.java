package drift.com.drift.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Attachment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 25/08/2017.
 */

public class AttachmentWrapper {

    private static String TAG = AttachmentWrapper.class.getSimpleName();

    public static void getAttachments(List<Integer> attachmentIds, final APICallbackWrapper<ArrayList<Attachment>> callback){

        HashMap<String, Object> imgixOptions = new HashMap<>();

        imgixOptions.put("img_auto", "compress");


        APIManager.getConversationClient().getAttachments(attachmentIds, imgixOptions).enqueue(new Callback<ArrayList<Attachment>>() {
            @Override
            public void onResponse(Call<ArrayList<Attachment>> call, Response<ArrayList<Attachment>> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Attachment>> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

}