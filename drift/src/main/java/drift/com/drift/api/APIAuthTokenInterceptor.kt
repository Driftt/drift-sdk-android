package drift.com.drift.api

import java.io.IOException

import drift.com.drift.model.Auth
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


internal class APIAuthTokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val auth = Auth.instance
        if (auth != null) {
            val url = request.url().newBuilder().addQueryParameter("access_token", auth.accessToken).build()
            request = request.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }

}
