package com.acmenxd.retrofit.interceptor;

import com.acmenxd.retrofit.exception.HttpNoWorkException;
import com.acmenxd.retrofit.utils.RetrofitUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/13 17:31
 * @detail 统一添加请求参数拦截器
 */
public final class NetworkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (RetrofitUtils.checkNetwork()) {
            return chain.proceed(chain.request());
        } else {
            throw new HttpNoWorkException("无网络连接");
        }
    }
}
