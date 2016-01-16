package com.corelibs.api;

import android.util.Log;

import com.corelibs.common.Configuration;
import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.RealResponseBody;

import java.io.BufferedReader;
import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;

/**
 * OkHttp的{@link Interceptor}, 通过设置
 * {@link Configuration#enableLoggingNetworkParams()}打印网络请求参数与响应结果
 * <br/>
 * Created by Ryan on 2015/12/31.
 */
public class HttpLoggingInterceptor implements Interceptor {
    private static final String TAG = "HttpLogging";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        long t1 = System.nanoTime();

        Buffer buffer = new Buffer();
        request.newBuilder().build().body().writeTo(buffer);

        Log.e(TAG, String.format("Sending request %s on %s%n%sRequest Params: %s",
                request.url(), chain.connection(), request.headers(), buffer.readUtf8()));
        buffer.close();

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        BufferedSource source = response.body().source();
        source.request(Long.MAX_VALUE);
        buffer = source.buffer().clone();
        Log.e(TAG, String.format("Received response for %s in %.1fms%n%sResponse Json: %s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers(),
                buffer.readUtf8()));

        return response;
    }
}