package com.acmenxd.retrofit.demo;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.acmenxd.retrofit.NetCodeUtils;
import com.acmenxd.retrofit.NetManager;
import com.acmenxd.retrofit.demo.net.NetCode;
import com.acmenxd.retrofit.exception.NetException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/11/22 14:36
 * @detail 顶级Application
 */
public final class BaseApplication extends Application {
    protected final String TAG = this.getClass().getSimpleName();

    private static BaseApplication sInstance = null;
    // 初始化状态 -> 默认false,初始化完成为true
    public boolean isInitFinish = false;

    public BaseApplication() {
        super();
        sInstance = this;
    }

    public static synchronized BaseApplication instance() {
        if (sInstance == null) {
            new RuntimeException("BaseApplication == null ?? you should extends BaseApplication in you app");
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        super.onCreate();
        // 初始化网络Retrofit
        initRetrofit();

        // 初始化完毕
        isInitFinish = true;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 应用配置变更~
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Net 配置
     */
    // 请求地址配置 -1:正式版  0->预发布  1->测试1  2->测试2  3->测试3
    public static final byte URL_Type = 1;
    public static final String BASE_URL;

    // 配置连接地址
    static {
        switch (URL_Type) {
            case -1:
                //正式版
                BASE_URL = "http://server.jeasonlzy.com/OkHttpUtils/";
                break;
            case 0:
                //预发布
                BASE_URL = "http://server.jeasonlzy.com/OkHttpUtils/";
                break;
            case 1:
                //测试1
                BASE_URL = "http://server.jeasonlzy.com/OkHttpUtils/";
                break;
            case 2:
                //测试2
                BASE_URL = "http://server.jeasonlzy.com/OkHttpUtils/";
                break;
            default:
                BASE_URL = "http://server.jeasonlzy.com/OkHttpUtils/";
                break;
        }
    }

    // Net Log 的开关
    public static final boolean NET_LOG_OPEN = true;
    // Net Log 的日志级别
    public static final int NET_LOG_LEVEL = Log.WARN;
    // Net Log 的日志Tag
    public static final String NET_LOG_TAG = "LogNet";
    // Net Log 的日志显示形式 -> 是否显示 "请求头 请求体 响应头 错误日志" 等详情
    public static final boolean NET_LOG_DETAILS = true;
    // Net Log 的日志显示形式 -> 是否显示请求过程中的日志,包含详细的请求头日志
    public static final boolean NET_LOG_DETAILS_All = false;
    // 网络缓存策略: 0->不启用缓存  1->遵从服务器缓存配置
    public static final int NET_CACHE_TYPE = 1;
    // 网络缓存大小(MB)
    public static final int NET_CACHE_SIZE = 10;
    // 网络连接超时时间(秒)
    public static final int CONNECT_TIMEOUT = 30;
    // 读取超时时间(秒)
    public static final int READ_TIMEOUT = 30;
    // 写入超时时间(秒)
    public static final int WRITE_TIMEOUT = 30;
    // 非Form表单形式的请求体,是否加入公共Body
    public static final boolean NOFORMBODY_CANADDBODY = false;
    // 公共请求参数
    public static final Map<String, String> ParameterMaps = new HashMap<>();
    // 公共Header(不允许相同Key值存在)
    public static final Map<String, String> HeaderMaps = new HashMap<>();
    // 公共Header(允许相同Key值存在)
    public static final Map<String, String> HeaderMaps2 = new HashMap<>();
    // 公共Body
    public static final Map<String, String> BodyMaps = new HashMap<>();

    // 配置请求公共需求
    static {
        ParameterMaps.put("parameter_key_1", "parameter_value_1");
        ParameterMaps.put("parameter_key_2", "parameter_value_2");
        HeaderMaps.put("header_key_1", "header_value_1");
        HeaderMaps.put("header_key_2", "header_value_2");
        BodyMaps.put("body_key_1", "body_value_1");
        BodyMaps.put("body_key_2", "body_value_2");
    }

    public void initRetrofit() {
        NetCodeUtils.startParseNetCode parseNetCode = new NetCodeUtils.startParseNetCode() {
            @Override
            public NetException parse(int code, String msg) {
                return NetCode.parseNetCode(code, msg);
            }
        };
        NetManager.newBuilder()
                .setContext(this)// 上下文对象
                .setParseNetCode(parseNetCode)// 统一处理NetCode回调
                .setBase_url(BASE_URL)// 基础URL地址
                .setNet_log_open(NET_LOG_OPEN)// Net Log 的开关
                .setNet_log_level(NET_LOG_LEVEL) // Net Log 的日志级别
                .setNet_log_tag(NET_LOG_TAG) // Net Log 的日志Tag
                .setNet_log_details(NET_LOG_DETAILS)// Net Log 的日志显示形式 -> 是否显示 "请求头 请求体 响应头 错误日志" 等详情
                .setNet_log_details_all(NET_LOG_DETAILS_All)// Net Log 的日志显示形式 -> 是否显示请求过程中的日志,包含详细的请求头日志
                .setNet_cache_dir(new File(BaseApplication.instance().getCacheDir(), "cache"))  // 网络缓存默认存储路径
                .setNet_cache_type(NET_CACHE_TYPE) // 网络缓存策略: 0->不启用缓存  1->遵从服务器缓存配置
                .setNet_cache_size(NET_CACHE_SIZE) // 网络缓存大小(MB)
                .setConnect_timeout(CONNECT_TIMEOUT)  // 网络连接超时时间(秒)
                .setRead_timeout(READ_TIMEOUT) // 读取超时时间(秒)
                .setWrite_timeout(WRITE_TIMEOUT)  // 写入超时时间(秒)
                .setNoformbody_canaddbody(NOFORMBODY_CANADDBODY) // 非Form表单形式的请求体,是否加入公共Body
                .setParameterMaps(ParameterMaps) // 公共请求参数
                .setHeaderMaps(HeaderMaps)  // 公共Header(不允许相同Key值存在)
                .setHeaderMaps2(HeaderMaps2)  // 公共Header(允许相同Key值存在)
                .setBodyMaps(BodyMaps)// 公共Body
                .build();
    }

}
