# Retrofit
基于<a href="https://github.com/square/retrofit">Retrofit</a>进行的封装

如要了解功能实现,请运行app程序查看控制台日志和源代码!
* 源代码 : <a href="https://github.com/AcmenXD/Retrofit">AcmenXD/Retrofit</a>
* apk下载路径 : <a href="https://github.com/AcmenXD/Resource/blob/master/apks/Retrofit.apk">Retrofit.apk</a>
### 依赖
---
- AndroidStudio
```
	allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
	}
```
```
	 compile 'com.github.AcmenXD:Retrofit:2.0'
```
### 混淆
---
```
     #rxjava & rxandroid
     -dontwarn sun.misc.**
     -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
         long producerIndex;
         long consumerIndex;
     }
     -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
         rx.internal.util.atomic.LinkedQueueNode producerNode;
     }
     -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
         rx.internal.util.atomic.LinkedQueueNode consumerNode;
     }
     #okhttp3
     -dontwarn okio.**
     -dontwarn okhttp3.**
     -dontwarn com.squareup.okhttp3.**
     #retrofit2
     -dontwarn retrofit2.**
     -keep class retrofit2.** { *; }
     -keepattributes Signature
     -keepattributes Exceptions
```
### 功能
---
- 统一添加公共parameter & header & body
- Log日志开关 + Log日志级别定义
- cookie自动管理
- 请求数据统一解析 -> NetEntity
- 请求统一回调 -> NetCallback | NetSubscriber
- 请求异常统一处理 -> NetError
- 支持Bitmap直接上传和下载
### 配置
---
**在Application中配置 > 只是个配置参考,除setContext和setBase_url必须设置外,其他都可用默认配置**
```java
// 基础url配置
public static final String BASE_URL = "http://xxx.com";
// Net Log 的开关
public static final boolean NET_LOG_OPEN = true;
// Net Log 的日志级别
public static final int NET_LOG_LEVEL = Log.WARN;
// Net Log 的日志Tag
public static final String NET_LOG_TAG = "NetLog";
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
// 添加公共信息
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
            .setContext(this)// 上下文对象(*必须设置)
            .setParseNetCode(parseNetCode)// 统一处理NetCode回调(如不设置则不会处理NetCode)
            .setBase_url(BASE_URL)// 基础URL地址(*必须设置)
            .setNet_log_open(NET_LOG_OPEN)// Net Log 的开关
            .setNet_log_level(NET_LOG_LEVEL) // Net Log 的日志级别
            .setNet_log_tag(NET_LOG_TAG) // Net Log 的日志Tag
            .setNet_log_details(NET_LOG_DETAILS)// Net Log 的日志显示形式 -> 是否显示 "请求头 请求体 响应头 错误日志" 等详情
            .setNet_log_details_all(NET_LOG_DETAILS_All)// Net Log 的日志显示形式 -> 是否显示请求过程中的日志,包含详细的请求头日志
            .setNet_cache_dir(new File(BaseApplication.instance().getCacheDir(), "NetCache"))  // 网络缓存默认存储路径
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
```
### 使用 -> 以下代码 注释很详细、很重要很重要很重要!!!
---
**定义请求接口类,retrofit注解请参考<a href="https://github.com/AcmenXD/Retrofit/blob/master/app/src/main/java/com/acmenxd/retrofit/demo/net/IRequestDoc.java">IRequestDoc</a>**
```java
public interface IAllRequest {
    /**
     * get请求
     */
    @GET("method")
    Call<NetEntity<TestEntity>> get(@Query("token") String token);

    /**
     * post请求
     */
    @FormUrlEncoded
    @POST("method")
    Observable<NetEntity<TestEntity>> post(@Field("token") String token);
}
```
---
**同步请求**
```java
/**
 * 注意的是网络请求一定要在子线程中完成，不能直接在UI线程执行
 */
final Call<NetEntity<TestEntity>> call = NetManager.INSTANCE.commonRequest(IAllRequest.class).get("token...");
new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            final NetEntity<TestEntity> response = call.execute().body();
            if (response != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTestResponse = response.getData();
                    }
                });
            }
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }
});
```
---
**Get请求**
```java
NetManager.INSTANCE.commonRequest(IAllRequest.class).get("token...").enqueue(
    newCallback(new NetCallback<NetEntity<TestEntity>>() {
        @Override
        public void succeed(NetEntity<TestEntity> pData) {
            int code = pData.getCode();
            String msg = pData.getMsg();
            mTestResponse = pData.getData();
            Logger.i("请求成功 -> url:" + mTestResponse.getUrl());
        }

        @Override
        public void failed(NetException pE) {
            super.failed(pE);
            int code = pE.getCode();
            String msg = pE.getMsg();
            String tostMsg = pE.getToastMsg();
        }
    }));
/**
 * 统一处理因异步导致的 Activity|Fragment销毁时发生NullPointerException问题
 *
 * @param pCallback Net请求回调
 */
public final <T> Callback<T> newCallback(final NetCallback<T> pCallback) {
    return new Callback<T>() {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!MainActivity.this.isFinishing()) {
                pCallback.onResponse(call, response);
            }
        }
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (!MainActivity.this.isFinishing()) {
                pCallback.onFailure(call, t);
            }
        }
    };
}
```
---
**Post请求**
```java
NetManager.INSTANCE.commonRequest(IAllRequest.class).post("token...")
    .compose(RxUtils.<NetEntity<TestEntity>>applySchedulers())
    //上面一行代码,等同于下面的这两行代码
    //.subscribeOn(Schedulers.io())
    //.observeOn(AndroidSchedulers.mainThread())
    .map(new Func1<NetEntity<TestEntity>, TestEntity>() {
        @Override
        public TestEntity call(NetEntity<TestEntity> pData) {
            code = pData.getCode();
            msg = pData.getMsg();
            mNetException = NetCode.parseNetCode(code, msg);
            return pData.getData();
        }
    })
    // 这里的true表示请求期间对数据进行过处理,这样Retrofit无法识别处理后的数据,所以需要开发者手动处理错误异常
    .subscribe(newSubscriber(new NetSubscriber<TestEntity>(true) {
        @Override
        public void succeed(final TestEntity pData) {
            // 服务器响应的code和msg统一交给NetCode处理
            NetException exception = NetCode.parseNetCode(code, msg);
            NetCodeUtils.netCodeResult(exception, new NetCodeUtils.NetCodeCallback() {
                @Override
                public void successData(NetExceptionSuccess pE) {
                    mTestResponse = pData;
                    Logger.i("请求成功 -> url:" + mTestResponse.getUrl());
                }

                @Override
                public void errorData(NetExceptionFail pE) {
                    failed(pE);
                }

                @Override
                public void unknownCode(NetExceptionUnknownCode pE) {
                    failed(pE);
                }
            });
        }

        @Override
        public void failed(NetException pE) {
            super.failed(pE);
            int code = pE.getCode();
            String msg = pE.getMsg();
            String tostMsg = pE.getToastMsg();
        }

        @Override
        public void finished() {

        }
    }));
/**
 * 统一处理因异步导致的 Activity|Fragment销毁时发生NullPointerException问题
 *
 * @param pSubscriber Net请求回调
 */
public final <T> Subscriber<T> newSubscriber(final NetSubscriber<T> pSubscriber) {
    return new Subscriber<T>() {
        @Override
        public void onCompleted() {
            if (!MainActivity.this.isFinishing()) {
                pSubscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (!MainActivity.this.isFinishing()) {
                pSubscriber.onError(e);
            }
        }

        @Override
        public void onNext(T pT) {
            if (!MainActivity.this.isFinishing()) {
                pSubscriber.onNext(pT);
            }
        }
    };
}
```
---
**其他请求方式及上传下载请移步https://github.com/AcmenXD/Retrofit 下载Demo查看**
---
### 打个小广告^_^
**gitHub** : https://github.com/AcmenXD   如对您有帮助,欢迎点Star支持,谢谢~

**技术博客** : http://blog.csdn.net/wxd_beijing
# END