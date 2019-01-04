package drift.com.drift.api

import java.util.ArrayList
import java.util.HashMap

import drift.com.drift.model.Attachment
import drift.com.drift.model.ConversationExtra
import drift.com.drift.model.Message

import drift.com.drift.model.MessageRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface APIConversationAPIBuilder {

    @GET("conversations/{conversationId}/messages")
    fun getMessages(@Path("conversationId") conversationId: Int?): Call<ArrayList<Message>>

    @GET("attachments")
    fun getAttachments(@Query("id") attachmentIds: List<Int>, @QueryMap imgixOptions: Map<String, Any>): Call<ArrayList<Attachment>>


    @GET("conversations/end_users/{endUserId}/extra")
    fun getConversationsForEndUser(@Path("endUserId") endUserId: Long?): Call<ArrayList<ConversationExtra>>

    @POST("conversations/{conversationId}/messages")
    fun postMessage(@Path("conversationId") conversationId: Int?, @Body messageRequest: MessageRequest): Call<Message>

    @POST("messages")
    fun createConversation(@Body payload: HashMap<String, Any>): Call<Message>

    @POST("https://conversation2.api.drift.com/messages/{messageId}/read")
    fun markMessageAsRead(@Path("messageId") messageId: Int?): Call<ResponseBody>

    @POST("https://conversation2.api.drift.com/messages/{messageId}/read-until")
    fun markMessageAsReadAlongWithPreviousMessages(@Path("messageId") messageId: Int?): Call<ResponseBody>

}
