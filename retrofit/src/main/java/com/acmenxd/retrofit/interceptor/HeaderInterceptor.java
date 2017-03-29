package com.acmenxd.retrofit.interceptor;

import com.acmenxd.retrofit.NetManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/13 18:11
 * @detail 统一添加Header拦截器
 */
public final class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        NetManager.Builder builder = NetManager.INSTANCE.getBuilder();
        Map<String, String> headers = builder.getHeaderMaps();
        Map<String, String> headers2 = builder.getHeaderMaps2();
        //添加请求公共Header
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                //header()如果有重名的将会覆盖
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        if (headers2 != null && headers2.size() > 0) {
            for (Map.Entry<String, String> entry : headers2.entrySet()) {
                //addHeader()允许相同key值的header存在
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
