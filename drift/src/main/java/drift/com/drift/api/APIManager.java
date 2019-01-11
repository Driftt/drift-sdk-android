package drift.com.drift.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import drift.com.drift.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class APIManager {

    private static final String API_CUSTOMER_URL = "https://customer.api.drift.com/";
    private static final String API_CONVERSATION_URL = "https://conversation.api.drift.com/";

    private static final String TAG = APIManager.class.getSimpleName();

    private static APIConversationAPIBuilder REST_CONVERSATION_CLIENT;
    private static APICustomerAPIBuilder REST_CUSTOMER_CLIENT;
    private static APIAuthlessBuilder REST_AUTHLESS_CLIENT;


    static {
        setupRestClient();
    }

    private APIManager() {
    }


    public static APIConversationAPIBuilder getConversationClient() {
        return REST_CONVERSATION_CLIENT;
    }
    public static APICustomerAPIBuilder getCustomerClient() {
        return REST_CUSTOMER_CLIENT;
    }

    public static APIAuthlessBuilder getAuthlessClient() {
        return REST_AUTHLESS_CLIENT;
    }


    public static Gson generateGson(){

        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DriftDateAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }


    private static String getUserAgentText() {

        String libVersionName = drift.com.drift.BuildConfig.VERSION_NAME;
        


        //"Drift-SDK/\(verion) (\(identifer); build:\(build); \(osName) \(osVersion)) \(alamofireVersion)"

        return "Drift-SDK-Android " + libVersionName;
    }

    private static void setupRestClient() {


        Gson gson = generateGson();
        OkHttpClient baseOkHttpClient = new OkHttpClient();

        String userAgent = getUserAgentText();

        OkHttpClient customerClient = baseOkHttpClient.newBuilder()
                .addInterceptor(new APIAuthTokenInterceptor())
                .addInterceptor(new UserAgentInterceptor(userAgent))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_CUSTOMER_URL)
                .callFactory(customerClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_CUSTOMER_CLIENT = retrofit.create(APICustomerAPIBuilder.class);

        OkHttpClient conversationClient = baseOkHttpClient.newBuilder()
                .addInterceptor(new APIAuthTokenInterceptor())
                .addInterceptor(new UserAgentInterceptor(userAgent))
                .build();

        Retrofit retrofitV2 = new Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(conversationClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_CONVERSATION_CLIENT = retrofitV2.create(APIConversationAPIBuilder.class);


        OkHttpClient authlessClient = baseOkHttpClient.newBuilder()
                .addInterceptor(new UserAgentInterceptor(userAgent))
                .build();

        Retrofit retrofitV3 = new Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(authlessClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_AUTHLESS_CLIENT = retrofitV3.create(APIAuthlessBuilder.class);


    }


}