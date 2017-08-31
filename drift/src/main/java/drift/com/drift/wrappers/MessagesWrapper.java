package drift.com.drift.wrappers;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;

import java.util.ArrayList;
import java.util.HashMap;

import drift.com.drift.api.APIManager;
import drift.com.drift.helpers.LoggerHelper;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.Message;
import drift.com.drift.model.MessageRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eoin on 11/08/2017.
 */

public class MessagesWrapper {

    private static String TAG = MessagesWrapper.class.getSimpleName();

    public static void getMessagesForConversationId(int conversationId, final APICallbackWrapper<ArrayList<Message>> callback){

        APIManager.getConversationClient().getMessages(conversationId).enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

    public static void sendMessageToConversation(int conversationId, MessageRequest message, final APICallbackWrapper<Message> callback){

        APIManager.getConversationClient().postMessage(conversationId, message).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                LoggerHelper.logMessage(TAG, t.getLocalizedMessage());
                callback.onResponse(null);
            }
        });
    }

    public static void createConversation(String bodyString, final APICallbackWrapper<Conversation> callback) {

        HashMap<String, String> payload = new HashMap<>();

        Editable editableText = new SpannableStringBuilder( bodyString );
        Linkify.addLinks(editableText, Linkify.WEB_URLS);
        String body = Html.toHtml(editableText).trim();

        payload.put("body", body);

        APIManager.getConversationClient().createConversation(payload).enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                if((response.code() != 200 && response.code() != 201) || response.body() == null) {
                    callback.onResponse(null);
                }else{
                    callback.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable throwable) {
                LoggerHelper.logMessage(TAG, throwable.getLocalizedMessage());
                callback.onResponse(null);
            }
        });

    }
}
