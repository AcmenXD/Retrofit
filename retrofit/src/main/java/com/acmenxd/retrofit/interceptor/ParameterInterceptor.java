package com.acmenxd.retrofit.interceptor;

import com.acmenxd.retrofit.HttpManager;
import com.acmenxd.retrofit.utils.RetrofitUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/13 17:31
 * @detail 统一添加请求参数拦截器
 */
public final class ParameterInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Map<String, String> parameters = new HashMap<>();
        if (HttpManager.INSTANCE.mutualCallback != null) {
            parameters = HttpManager.INSTANCE.mutualCallback.getParameters(original.url().toString());
        }
        HttpUrl.Builder builder = original.url().newBuilder();
        //添加请求公共参数
        if (parameters != null && parameters.size() > 0) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (entry != null && !RetrofitUtils.isEmpty(entry.getKey()) && !RetrofitUtils.isEmpty(entry.getValue())) {
                    builder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
        }
        Request request = original.newBuilder()
                .url(builder.build())
                .build();
        return chain.proceed(request);
    }
}
