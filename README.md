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
	 compile 'com.github.AcmenXD:Retrofit:3.0'
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
- 3.0完善很多功能,并修复诸多bug
- 统一添加公共parameter & header & body
- Log日志级别定义
- cookie自动管理
- 请求数据统一解析 -> HttpEntity
- 请求统一回调 -> HttpCallback | HttpSubscriber
- 请求异常统一处理 -> HttpError
- 支持Bitmap直接上传和下载
### 配置
---
**在Application中配置(包含Logger & SpTool的配置) > 只是个配置参考,除context和base_url必须设置外,其他都可用默认配置**
```java
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
    Call<TestHttpEntity> get(@Query("token") String token);

    /**
     * post请求
     */
    @FormUrlEncoded
    @POST("method")
    Observable<HttpGenericityEntity<TestEntity>> post(@Field("token") String token);
}
```
---
**同步请求**
```java
/**
 * 注意的是网络请求一定要在子线程中完成，不能直接在UI线程执行
 */
final Call<TestHttpEntity> call = HttpManager.INSTANCE.request(IAllRequest.class).get("token...");
new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            final TestHttpEntity response = call.execute().body();
            if (response != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
NetManager.INSTANCE.request(IAllRequest.class).get("token...")
    .enqueue(new HttpCallback<TestHttpEntity>() {
        @Override
        public void succeed(@NonNull TestHttpEntity pData) {
            int code = pData.getCode();
            String msg = pData.getMsg();
            Logger.i("请求成功 -> url:" + pData.data.url);
        }

        @Override
        public void finished() {
        }
    });
```
---
**Post请求**
```java
NetManager.INSTANCE.request(IAllRequest.class).post("token...")
    .compose(RxUtils.<HttpGenericityEntity<TestEntity>>applySchedulers())
    //上面一行代码,等同于下面的这两行代码
    //.subscribeOn(Schedulers.io())
    //.observeOn(AndroidSchedulers.mainThread())
    .map(new Func1<HttpGenericityEntity<TestEntity>, TestEntity>() {
        @Override
        public TestEntity call(HttpGenericityEntity<TestEntity> pData) {
            code = pData.getCode();
            msg = pData.getMsg();
            return pData.getData();
        }
    })
    // 这里的true表示请求期间对数据进行过处理,这样Retrofit无法识别处理后的数据,所以需要开发者手动处理错误异常
    .subscribe(new HttpSubscriber<TestEntity>() {
        @Override
        public void succeed(final @NonNull TestEntity pData) {

        }

        @Override
        public void finished() {
        }
    });
```
---
**其他请求方式及上传下载请移步https://github.com/AcmenXD/Retrofit 下载Demo查看**
---
**gitHub** : https://github.com/AcmenXD   如对您有帮助,欢迎点Star支持,谢谢~

**技术博客** : http://blog.csdn.net/wxd_beijing
# END