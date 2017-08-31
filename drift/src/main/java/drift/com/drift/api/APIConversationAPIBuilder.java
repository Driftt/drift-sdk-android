package drift.com.drift.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import drift.com.drift.model.Attachment;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.ConversationExtra;
import drift.com.drift.model.Message;

import drift.com.drift.model.MessageRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;



public interface APIConversationAPIBuilder {


    @GET("conversations/{conversationId}")
    Call<Conversation> getConversation(@Path("conversationId") Integer converationId);

    @GET("conversations/{conversationId}/messages")
    Call<ArrayList<Message>> getMessages(@Path("conversationId") Integer conversationId);

    @GET("attachments")
    Call<ArrayList<Attachment>> getAttachments(@Query("id") List<Integer> attachmentIds, @QueryMap Map<String ,Object> imgixOptions);

//    @POST("conversations/{conversationId}/messages")
//    Call<Message> postMessage(@Path("conversationId") Integer conversationId, @Body MessageRequest messageRequest);
//
//    @Multipart
//    @POST("attachments")
//    Call<Attachment> uploadAttachment(@Part("conversationId") RequestBody description, @Part MultipartBody.Part file);

    @GET("conversations/end_users/{endUserId}/extra")
    Call<ArrayList<ConversationExtra>> getConversationsForEndUser(@Path("endUserId") int endUserId);

    @POST("conversations/{conversationId}/messages")
    Call<Message> postMessage(@Path("conversationId") Integer conversationId, @Body MessageRequest messageRequest);

    @POST("messages")
    Call<Conversation> createConversation(@Body HashMap<String, String> payload);

    @POST("https://conversation2.api.drift.com/messages/{messageId}/read")
    Call<ResponseBody> markMessageAsRead(@Path("messageId") Integer messageId);

    @POST("https://conversation2.api.drift.com/messages/{messageId}/read-until")
    Call<ResponseBody> markMessageAsReadAlongWithPreviousMessages(@Path("messageId") Integer messageId);

}
