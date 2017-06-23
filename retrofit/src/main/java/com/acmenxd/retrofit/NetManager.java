package com.acmenxd.retrofit;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.acmenxd.logger.LogTag;
import com.acmenxd.logger.LogType;
import com.acmenxd.logger.Logger;
import com.acmenxd.retrofit.converter.CustomConverterFactory;
import com.acmenxd.retrofit.cookie.NetCookieJar;
import com.acmenxd.retrofit.interceptor.BodyInterceptor;
import com.acmenxd.retrofit.interceptor.GzipInterceptor;
import com.acmenxd.retrofit.interceptor.HeaderInterceptor;
import com.acmenxd.retrofit.interceptor.LoggerInterceptor;
import com.acmenxd.retrofit.interceptor.ParameterInterceptor;
import com.acmenxd.sptool.SpManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/28 11:46
 * @detail 网络请求总类
 */
public enum NetManager {
    INSTANCE;

    private Builder mBuilder;
    private Retrofit mRetrofit;
    private Object mAllRequest;

    /**
     * 初始化
     */
    private void init(@NonNull Builder pBuilder) {
        mBuilder = pBuilder;
        //初始化Retrofit
        mRetrofit = createRetrofit(createClient(mBuilder.getConnect_timeout(), mBuilder.getRead_timeout(), mBuilder.getWrite_timeout()));
    }

    /**
     * 获取IAllRequest实例
     */
    public <T> T commonRequest(@NonNull Class<T> pIRequest) {
        if (mAllRequest == null) {
            mAllRequest = mRetrofit.create(pIRequest);
        }
        return (T) mAllRequest;
    }

    /**
     * 根据IRequest类获取Request实例
     */
    public <T> T request(@NonNull Class<T> pIRequest) {
        mAllRequest = null;
        return mRetrofit.create(pIRequest);
    }

    /**
     * 创建新的Retrofit实例
     * 根据IRequest类获取Request实例
     */
    public <T> T newRequest(@NonNull Class<T> pIRequest) {
        return newRequest(mBuilder.getConnect_timeout(), mBuilder.getRead_timeout(), mBuilder.getWrite_timeout(), pIRequest);
    }

    /**
     * 创建新的Retrofit实例,并设置超时时间
     * 根据IRequest类获取Request实例
     */
    public <T> T newRequest(@IntRange(from = 0) int connectTimeout, @IntRange(from = 0) int readTimeout, @IntRange(from = 0) int writeTimeout, @NonNull Class<T> pIRequest) {
        return createRetrofit(createClient(connectTimeout, readTimeout, writeTimeout)).create(pIRequest);
    }

    /**
     * 创建 Retrofit实例
     */
    private Retrofit createRetrofit(@NonNull OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                // 设置baseUrl
                .baseUrl(mBuilder.getBase_url())
                // 使用RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                // 网络数据解析总类
                .addConverterFactory(CustomConverterFactory.create())
                // 设置OkHttpClient
                .client(client)
                // 构建
                .build();
        return retrofit;
    }

    /**
     * 创建 OkHttpClient实例
     */
    private OkHttpClient createClient(@IntRange(from = 0) int connectTimeout, @IntRange(from = 0) int readTimeout, @IntRange(from = 0) int writeTimeout) {
        OkHttpClient.Builder mClientBuilder = new OkHttpClient.Builder();
        // 添加参数
        mClientBuilder.addInterceptor(new ParameterInterceptor());
        // 添加请求头
        mClientBuilder.addInterceptor(new HeaderInterceptor());
        // 添加Body参数
        mClientBuilder.addInterceptor(new BodyInterceptor());
        // 设置Log日志 -> 需在Gzip前面,否则输出信息因为Gzip压缩导致乱码
        if (mBuilder.isNet_log_details_all()) {
            // 如启用此日志方式,Gzip也开启的情况下,输入日志会有乱码
            mClientBuilder.addNetworkInterceptor(new LoggerInterceptor());
        } else {
            mClientBuilder.addInterceptor(new LoggerInterceptor());
        }
        // 启用Gzip压缩
        // mClientBuilder.addInterceptor(new GzipInterceptor());
        // 设置缓存
        if (mBuilder.getNet_cache_type() != 0) {
            // mBuilder.addNetworkInterceptor(new NetCacheInterceptor3()); // 功能尚未完成,无法使用
            mClientBuilder.cache(new Cache(mBuilder.getNet_cache_dir(), 1024 * 1024 * mBuilder.getNet_cache_size()));
        }
        // 启用cookie -> 参考http://www.jianshu.com/p/1a5f14b63f47
        mClientBuilder.cookieJar(NetCookieJar.create());
        // 失败重试
        mClientBuilder.retryOnConnectionFailure(true);
        // 设置超时时间
        mClientBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        mClientBuilder.readTimeout(readTimeout, TimeUnit.SECONDS);
        mClientBuilder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
//        /**
//         * 添加证书
//         */
//        mBuilder.certificatePinner(new CertificatePinner.Builder()
//                .add("YOU API.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
//                .add("YOU API..com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
//                .add("YOU API..com", "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=")
//                .add("YOU API..com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
//                .build());
//        /**
//         * 设置代理
//         */
//        mBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
//        /**
//         * 支持https
//         * http://blog.csdn.net/sk719887916/article/details/51597816
//         */
//        SLSocketFactory sslSocketFactory = getSSLSocketFactory_Certificate(context, "BKS", R.raw.srca);
        return mClientBuilder.build();
    }

    public static class Builder {
        // 上下文对象
        private Context context;
        // 统一处理NetCode回调
        private NetCodeUtils.startParseNetCode parseNetCode;
        // 基础URL地址
        private String base_url = "";
        // Net Log 的开关
        private boolean net_log_open = true;
        // Net Log 的日志级别
        private LogType net_log_level = LogType.V;
        // Net Log 的日志Tag
        private LogTag net_log_tag = LogTag.mk("LogNet");
        // Net Log 的日志显示形式 -> 是否显示 "请求头 请求体 响应头 错误日志" 等详情
        private boolean net_log_details = true;
        // Net Log 的日志显示形式 -> 是否显示请求过程中的日志,包含详细的请求头日志
        private boolean net_log_details_all = false;
        // 网络缓存默认存储路径
        private File net_cache_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NetCache/");
        // 网络缓存策略: 0->不启用缓存  1->遵从服务器缓存配置
        private int net_cache_type = 1;
        // 网络缓存大小(MB)
        private int net_cache_size = 10;
        // 网络连接超时时间(秒)
        private int connect_timeout = 30;
        // 读取超时时间(秒)
        private int read_timeout = 30;
        // 写入超时时间(秒)
        private int write_timeout = 30;
        // 非Form表单形式的请求体,是否加入公共Body
        private boolean noformbody_canaddbody = false;
        // 公共请求参数
        private Map<String, String> parameterMaps = new HashMap<>();
        // 公共Header(不允许相同Key值存在)
        private Map<String, String> headerMaps = new HashMap<>();
        // 公共Header(允许相同Key值存在)
        private Map<String, String> headerMaps2 = new HashMap<>();
        // 公共Body
        private Map<String, String> bodyMaps = new HashMap<>();

        public Builder setContext(@NonNull Context pContext) {
            context = pContext;
            return this;
        }

        public Builder setParseNetCode(@NonNull NetCodeUtils.startParseNetCode pParseNetCode) {
            parseNetCode = pParseNetCode;
            return this;
        }

        public Builder setBase_url(@NonNull String pBase_url) {
            base_url = pBase_url;
            return this;
        }

        public Builder setNet_log_open(boolean pNet_log_open) {
            net_log_open = pNet_log_open;
            return this;
        }

        public Builder setNet_log_level(@IntRange(from = 0) int pLogLevel) {
            LogType[] types = LogType.values();
            for (int i = 0, len = types.length; i < len; i++) {
                if (pLogLevel == types[i].intValue()) {
                    net_log_level = types[i];
                }
            }
            return this;
        }

        public Builder setNet_log_tag(@NonNull String pNet_log_tag) {
            net_log_tag = LogTag.mk(pNet_log_tag);
            return this;
        }

        public Builder setNet_log_details(boolean pNet_log_details) {
            net_log_details = pNet_log_details;
            return this;
        }

        public Builder setNet_log_details_all(boolean pNet_log_details_all) {
            net_log_details_all = pNet_log_details_all;
            return this;
        }

        public Builder setNet_cache_dir(@NonNull File pNet_cache_dir) {
            net_cache_dir = pNet_cache_dir;
            return this;
        }

        public Builder setNet_cache_type(@IntRange(from = 0) int pNet_cache_type) {
            net_cache_type = pNet_cache_type;
            return this;
        }

        public Builder setNet_cache_size(@IntRange(from = 0) int pNet_cache_size) {
            net_cache_size = pNet_cache_size;
            return this;
        }

        public Builder setConnect_timeout(@IntRange(from = 0) int pConnect_timeout) {
            connect_timeout = pConnect_timeout;
            return this;
        }

        public Builder setRead_timeout(@IntRange(from = 0) int pRead_timeout) {
            read_timeout = pRead_timeout;
            return this;
        }

        public Builder setWrite_timeout(@IntRange(from = 0) int pWrite_timeout) {
            write_timeout = pWrite_timeout;
            return this;
        }

        public Builder setNoformbody_canaddbody(boolean pNoformbody_canaddbody) {
            noformbody_canaddbody = pNoformbody_canaddbody;
            return this;
        }

        public Builder setParameterMaps(@NonNull Map<String, String> pParameterMaps) {
            parameterMaps = pParameterMaps;
            return this;
        }

        public Builder setHeaderMaps(@NonNull Map<String, String> pHeaderMaps) {
            headerMaps = pHeaderMaps;
            return this;
        }

        public Builder setHeaderMaps2(@NonNull Map<String, String> pHeaderMaps2) {
            headerMaps2 = pHeaderMaps2;
            return this;
        }

        public Builder setBodyMaps(@NonNull Map<String, String> pBodyMaps) {
            bodyMaps = pBodyMaps;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public NetCodeUtils.startParseNetCode getParseNetCode() {
            return parseNetCode;
        }

        public String getBase_url() {
            return base_url;
        }

        public boolean isNet_log_open() {
            return net_log_open;
        }

        public LogType getNet_log_level() {
            return net_log_level;
        }

        public LogTag getNet_log_tag() {
            return net_log_tag;
        }

        public boolean isNet_log_details() {
            return net_log_details;
        }

        public boolean isNet_log_details_all() {
            return net_log_details_all;
        }

        public File getNet_cache_dir() {
            return net_cache_dir;
        }

        public int getNet_cache_type() {
            return net_cache_type;
        }

        public int getNet_cache_size() {
            return net_cache_size;
        }

        public int getConnect_timeout() {
            return connect_timeout;
        }

        public int getRead_timeout() {
            return read_timeout;
        }

        public int getWrite_timeout() {
            return write_timeout;
        }

        public boolean isNoformbody_canaddbody() {
            return noformbody_canaddbody;
        }

        public Map<String, String> getParameterMaps() {
            return parameterMaps;
        }

        public Map<String, String> getHeaderMaps() {
            return headerMaps;
        }

        public Map<String, String> getHeaderMaps2() {
            return headerMaps2;
        }

        public Map<String, String> getBodyMaps() {
            return bodyMaps;
        }

        public void build() {
            /**
             * 初始化
             * context必须设置
             */
            Logger.setContext(context);
            /**
             * 初始化
             * context必须设置
             */
            SpManager.setContext(context);
            /**
             * 初始化 -> 配置完成后必须调用此函数生效
             */
            SpManager.init();
            /**
             * 配置完成,初始化Retrofit
             */
            NetManager.INSTANCE.init(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder getBuilder() {
        if (mBuilder == null) {
            mBuilder = newBuilder();
        }
        return mBuilder;
    }

}
