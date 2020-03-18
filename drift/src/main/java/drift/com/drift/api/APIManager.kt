package drift.com.drift.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.Date

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Build
import drift.com.drift.Drift



internal object APIManager {

    private const val API_CUSTOMER_URL = "https://customer.api.drift.com/"
    private const val API_CONVERSATION_URL = "https://conversation.api.drift.com/"
    private const val API_MESSAGING_URL = "https://messaging.api.drift.com/"
    private const val API_MEETING_URL = "https://meetings.api.drift.com/"



    lateinit var conversationClient: APIConversationAPIBuilder
    lateinit var messagingClient: APIMessagingAPIBulder
    lateinit var customerClient: APICustomerAPIBuilder
    lateinit var meetingClient: APIMeetingAPIBuilder
    lateinit var authlessClient: APIAuthlessBuilder

    init {
        setupRestClient()
    }


    fun generateGson(): Gson {

        return GsonBuilder()
                .registerTypeAdapter(Date::class.java, DriftDateAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    private fun getUserAgentText(): String {

        val libVersionName = drift.com.drift.BuildConfig.VERSION_NAME

        val packageName = Drift.getContext()?.packageName ?: ""
        val osVersion = Build.VERSION.SDK_INT
        val deviceName = android.os.Build.MODEL
        val manufacturer = Build.MANUFACTURER
        return "Drift-SDK-Android/$libVersionName ($packageName; $osVersion; $manufacturer; $deviceName; )"
    }

    private fun setupRestClient() {


        val gson = generateGson()
        val baseOkHttpClient = OkHttpClient()

        val userAgent = getUserAgentText()


        val customerClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .addInterceptor(UserAgentInterceptor(userAgent))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(API_CUSTOMER_URL)
                .callFactory(customerClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.customerClient = retrofit.create(APICustomerAPIBuilder::class.java)

        val conversationClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .addInterceptor(UserAgentInterceptor(userAgent))
                .build()

        val retrofitV2 = Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(conversationClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.conversationClient = retrofitV2.create(APIConversationAPIBuilder::class.java)


        val authlessClient = baseOkHttpClient.newBuilder()
                .addInterceptor(UserAgentInterceptor(userAgent))
                .build()

        val retrofitV3 = Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(authlessClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.authlessClient = retrofitV3.create(APIAuthlessBuilder::class.java)

        val messagingClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .addInterceptor(UserAgentInterceptor(userAgent))
                .build()

        val retrofitV4 = Retrofit.Builder()
                .baseUrl(API_MESSAGING_URL)
                .callFactory(messagingClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.messagingClient = retrofitV4.create(APIMessagingAPIBulder::class.java)

        val meetingClient = baseOkHttpClient.newBuilder()
                .addInterceptor(APIAuthTokenInterceptor())
                .addInterceptor(UserAgentInterceptor(userAgent))
                .build()

        val retrofitV5 = Retrofit.Builder()
                .baseUrl(API_MEETING_URL)
                .callFactory(meetingClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        this.meetingClient = retrofitV5.create(APIMeetingAPIBuilder::class.java)

    }


}