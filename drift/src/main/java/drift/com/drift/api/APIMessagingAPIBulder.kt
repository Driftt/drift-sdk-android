package drift.com.drift.api

import drift.com.drift.model.Message
import drift.com.drift.model.MessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.HashMap

internal interface APIMessagingAPIBulder {

    @POST("conversations/{conversationId}/messages")
    fun postMessage(@Path("conversationId") conversationId: Long?, @Body messageRequest: MessageRequest): Call<Message>

    @POST("messages")
    fun createConversation(@Body payload: HashMap<String, Any>): Call<Message>


}