package drift.com.drift.api;

import java.io.IOException;

import drift.com.drift.model.Auth;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


class APIAuthTokenInterceptor implements Interceptor {

    private static final String TAG = APIAuthTokenInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Auth auth = Auth.getInstance();
        if (auth != null && auth.getAccessToken() != null) {
            HttpUrl url = request.url().newBuilder().addQueryParameter("access_token", auth.getAccessToken()).build();
            request = request.newBuilder().url(url).build();
        }
        return chain.proceed(request);
    }

}
