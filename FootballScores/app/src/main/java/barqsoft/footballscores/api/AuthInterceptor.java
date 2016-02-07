package barqsoft.footballscores.api;

import java.io.IOException;

import barqsoft.footballscores.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Custom interceptor for authenticating with Football-Data API
 */
class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY)
                .build();
        return chain.proceed(request);
    }
}
