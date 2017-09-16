package com.acmenxd.retrofit.demo;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Environment;

import com.acmenxd.logger.LogTag;
import com.acmenxd.logger.LogType;
import com.acmenxd.logger.Logger;
import com.acmenxd.retrofit.HttpManager;
import com.acmenxd.retrofit.HttpMutualCallback;
import com.acmenxd.retrofit.demo.http.ResultCallback;
import com.acmenxd.retrofit.demo.utils.EncodeDecode;
import com.acmenxd.sptool.SpEncodeDecodeCallback;
import com.acmenxd.sptool.SpManager;

import java.io.File;
import java.io.IOException;
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
    // 记录启动时间
    public long startTime = 0;

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
        startTime = System.currentTimeMillis();
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
    // 请求地址配置 -1:正式版  0->预发布  1->测试1  2->测试2
    public static final byte URL_Type = 1;
    // 基础url配置
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

    public void initRetrofit() {
        //------------------------------------Logger配置---------------------------------
        Logger.LOG_OPEN = true;
        Logger.LOG_LEVEL = LogType.V;
        Logger.APP_PKG_NAME = this.getPackageName();
        Logger.LOGFILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Logger/";
        //------------------------------------SpTool配置---------------------------------
        // 设置全局Sp实例,项目启动时创建,并通过getCommonSp拿到,项目中只有一份实例
        SpManager.CommonSp = new String[]{"spCookie"};
        // 加解密回调 - 不设置或null表示不进行加解密处理
        SpManager.setEncodeDecodeCallback(new SpEncodeDecodeCallback() {
            @Override
            public String encode(String pStr) {
                String result = null;
                try {
                    result = EncodeDecode.encode(pStr);
                } catch (IOException pE) {
                    pE.printStackTrace();
                }
                return result;
            }

            @Override
            public String decode(String pStr) {
                String result = null;
                try {
                    result = EncodeDecode.decode(pStr);
                } catch (IOException pE) {
                    pE.printStackTrace();
                } catch (ClassNotFoundException pE) {
                    pE.printStackTrace();
                }
                return result;
            }
        });
        // * 必须设置,否则无法使用
        SpManager.setContext(this);
        //------------------------------------Retrofit配置---------------------------------
        // * 必须设置,否则无法使用 - 上下文对象(*必须设置)
        HttpManager.INSTANCE.context = this;
        // * 必须设置,否则无法使用 - 基础URL地址(*必须设置)
        HttpManager.INSTANCE.base_url = BASE_URL;
        // Net Log 的日志Tag
        HttpManager.INSTANCE.net_log_tag = LogTag.mk("NetLog");
        // Net Log 的日志显示形式 -> 是否显示 "请求头 请求体 响应头 错误日志" 等详情
        HttpManager.INSTANCE.net_log_details = true;
        // Net Log 的日志显示形式 -> 是否显示请求过程中的日志,包含详细的请求头日志
        HttpManager.INSTANCE.net_log_details_all = false;
        // 非Form表单形式的请求体,是否加入公共Body
        HttpManager.INSTANCE.noformbody_canaddbody = false;
        // 网络缓存默认存储路径
        HttpManager.INSTANCE.net_cache_dir = new File(this.getCacheDir(), "NetCache");
        // 网络缓存策略: 0->不启用缓存  1->遵从服务器缓存配置
        HttpManager.INSTANCE.net_cache_type = 0;
        // 网络缓存大小(MB)
        HttpManager.INSTANCE.net_cache_size = 0;
        // 网络连接超时时间(秒)
        HttpManager.INSTANCE.connect_timeout = 10;
        // 读取超时时间(秒)
        HttpManager.INSTANCE.read_timeout = 30;
        // 写入超时时间(秒)
        HttpManager.INSTANCE.write_timeout = 30;
        /**
         * 设置请求返回时回调
         */
        HttpManager.INSTANCE.resultCallback = new ResultCallback();
        /**
         * 设置Net公共参数 -> 为动态配置而设置的此函数
         */
        HttpManager.INSTANCE.mutualCallback = new HttpMutualCallback() {
            @Override
            public Map<String, String> getBodys(String url, Map<String, String> oldMaps) {
                return null;
            }

            @Override
            public Map<String, String> getParameters(String url) {
                Map<String, String> maps = new HashMap<>();
                maps.put("parameter_key_1", "parameter_value_1");
                maps.put("parameter_key_2", "parameter_value_2");
                return maps;
            }

            @Override
            public Map<String, String> getHeaders(String url) {
                return null;
            }

            @Override
            public Map<String, String> getReHeaders(String url) {
                return null;
            }
        };
    }
}
