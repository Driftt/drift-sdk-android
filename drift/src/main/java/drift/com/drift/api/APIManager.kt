package drift.com.drift.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.Date

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIManager {

    private val API_CUSTOMER_URL = "https://customer.api.drift.com/"
    private val API_CONVERSATION_URL = "https://conversation.api.drift.com/"

    var conversationClient: APIConversationAPIBuilder? = null
        private set
    var customerClient: APICustomerAPIBuilder? = null
        private set
    var authlessClient: APIAuthlessBuilder? = null
        private set


    init {
        setupRestClient()
    }


    fun generateGson(): Gson {

        return GsonBuilder()
                .registerTypeAdapter(Date::class.java, DriftDateAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    private fun setupRestClient() {


        val gson = generateGson()
        val baseOkHttpClient = OkHttpClient()


        val customerClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(API_CUSTOMER_URL)
                .callFactory(customerClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.customerClient = retrofit.create(APICustomerAPIBuilder::class.java)

        val conversationClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .build()

        val retrofitV2 = Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(conversationClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.conversationClient = retrofitV2.create(APIConversationAPIBuilder::class.java)


        val authlessClient = baseOkHttpClient.newBuilder()
                .build()

        val retrofitV3 = Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(authlessClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.authlessClient = retrofitV3.create(APIAuthlessBuilder::class.java)


    }


}