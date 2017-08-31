package drift.com.drift.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

//        Type token = new TypeToken<RealmList<RealmInt>>(){}.getType();

        Gson gson = new GsonBuilder()
//                .setExclusionStrategies(new ExclusionStrategy() {
//                    @Override
//                    public boolean shouldSkipField(FieldAttributes f) {
//                        return f.getDeclaringClass().equals(RealmObject.class);
//                    }
//
//                    @Override
//                    public boolean shouldSkipClass(Class<?> clazz) {
//                        return false;
//                    }
//                })
                .registerTypeAdapter(Date.class, new DriftDateAdapter())
//                .registerTypeAdapter(token, new IntegerListAdapter())
                .create();
        return gson;
    }

    private static void setupRestClient() {


        Gson gson = generateGson();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        if (!BuildConfig.DEBUG) {
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        }

        OkHttpClient customerClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new APIAuthTokenInterceptor())
//                .addInterceptor(new HardAuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_CUSTOMER_URL)
                .callFactory(customerClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_CUSTOMER_CLIENT = retrofit.create(APICustomerAPIBuilder.class);

        OkHttpClient conversationClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new APIAuthTokenInterceptor())
//                .addInterceptor(new HardAuthInterceptor())
                .build();

        Retrofit retrofitV2 = new Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(conversationClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_CONVERSATION_CLIENT = retrofitV2.create(APIConversationAPIBuilder.class);


        OkHttpClient authlessClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofitV3 = new Retrofit.Builder()
                .baseUrl(API_CONVERSATION_URL)
                .callFactory(authlessClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        REST_AUTHLESS_CLIENT = retrofitV3.create(APIAuthlessBuilder.class);


    }


}