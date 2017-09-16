package com.acmenxd.retrofit.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.acmenxd.logger.Logger;
import com.acmenxd.retrofit.HttpEntity;
import com.acmenxd.retrofit.HttpGenericityEntity;
import com.acmenxd.retrofit.HttpManager;
import com.acmenxd.retrofit.callback.HttpCallback;
import com.acmenxd.retrofit.callback.HttpSubscriber;
import com.acmenxd.retrofit.demo.http.IAllRequest;
import com.acmenxd.retrofit.demo.http.IDownloadRequest;
import com.acmenxd.retrofit.demo.http.IUploadRequest;
import com.acmenxd.retrofit.demo.http.LoadHelper;
import com.acmenxd.retrofit.demo.model.TestEntity;
import com.acmenxd.retrofit.demo.model.TestHttpEntity;
import com.acmenxd.retrofit.demo.utils.RxUtils;
import com.acmenxd.retrofit.demo.utils.Utils;
import com.acmenxd.retrofit.exception.HttpException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Subscription;
import rx.functions.Func1;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/12/16 15:34
 * @detail something
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    private ImageView iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Retrofit使用演示");
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.imageView);

        /**
         * 同步请求
         * 注意的是网络请求一定要在子线程中完成，不能直接在UI线程执行
         */
        final Call<TestHttpEntity> call = request().get("token...");
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
    }

    /**
     * * 开放重写,满足不同需求
     */
    public IAllRequest request() {
        return request(IAllRequest.class);
    }

    /**
     * 根据IRequest类获取Request实例
     */
    public final <E> E request(@NonNull Class<E> pIRequest) {
        return HttpManager.INSTANCE.request(pIRequest);
    }

    /**
     * get请求
     */
    public void getClick(View view) {
        request().get("token...")
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
    }

    /**
     * options获取服务器支持的http请求方式
     */
    public void optionsClick(View view) {
        request().options("token...")
                .enqueue(new HttpCallback<TestHttpEntity>() {
                    @Override
                    public void succeed(@NonNull TestHttpEntity pData) {
                        Logger.i("请求成功 -> url:");
                    }

                    @Override
                    public void finished() {
                    }
                });

    }

    private int code;
    private String msg;
    private HttpException mNetException;

    /**
     * post请求
     */
    public void postClick(View view) {
        Subscription subscription = request().post("token...")
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
    }

    /**
     * put用法同post主要用于创建资源
     */
    public void putClick(View view) {
        request().put("token...", new TestEntity())
                .enqueue(new HttpCallback<HttpGenericityEntity<TestEntity>>() {
                    @Override
                    public void succeed(@NonNull HttpGenericityEntity<TestEntity> pData) {
                        int code = pData.getCode();
                        String msg = pData.getMsg();
                        Logger.i("请求成功 -> url:");
                    }

                    @Override
                    public void finished() {
                    }
                });
    }

    /**
     * 下载请求
     */
    public void downloadClick(View view) {
        request(IDownloadRequest.class).getRx("http://server.jeasonlzy.com/OkHttpUtils/image")
//        request(IDownloadRequest.class).download("http://10.1.22.49:8080/webDemo/aa")
                .compose(RxUtils.<ResponseBody>applySchedulers())
                .subscribe(new HttpSubscriber<ResponseBody>() {
                    @Override
                    public void succeed(@NonNull ResponseBody pData) {
                        boolean result = LoadHelper.saveDownLoadFile(pData, Environment.getExternalStorageDirectory().getAbsolutePath() + "/download.jpg");
                    }
                });
//        request(IDownloadRequest.class).download("http://server.jeasonlzy.com/OkHttpUtils/image")
//                .enqueue(newCallback(new NetCallback<ResponseBody>() {
//                    @Override
//                    public void succeed(ResponseBody pData) {
//                        boolean result = LoadUtils.saveDownLoadFile(pData, FileUtils.cacheDirPath + "download.jpg");
//                    }
//                }));
    }

    /**
     * bitmap请求
     */
    public void bitmapClick(View view) {
        request().image("token...")
                .enqueue(new HttpCallback<Bitmap>() {
                    @Override
                    public void succeed(@NonNull Bitmap pData) {
                        iv.setImageBitmap(pData);
                    }
                });
    }

    /**
     * bitmap上传请求
     */
    public void upBitmapClick(View view) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);
        request().upImage("token...", bitmap)
                .enqueue(new HttpCallback<HttpEntity>() {
                    @Override
                    public void succeed(@NonNull HttpEntity pData) {
                        int code = pData.getCode();
                        String msg = pData.getMsg();
                        Logger.i("请求成功 -> msg:" + pData.getMsg());
                    }
                });
    }

    /**
     * 上传
     */
    public void uploadClick(View view) {
        Utils.showFileChooser(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = Utils.showFileChooser_onActivityResult(this, requestCode, resultCode, data);
        if(Utils.isEmpty(path)){
            Toast.makeText(this, "文件获取失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);

        HashMap<String, String> dataStrs = new HashMap<>();
        dataStrs.put("name_key_1", "name_value_1");
        dataStrs.put("name_key_2", "name_value_2");
        request(IUploadRequest.class)
                .uploadRx("http://server.jeasonlzy.com/OkHttpUtils/" + "upload", LoadHelper.getUploadFiles(file, file), LoadHelper.getUploadParams(dataStrs))// 第一种方法
//                .upload("http://server.jeasonlzy.com/OkHttpUtils/" + "upload", LoadHelper.getRequestBody(dataStrs, file, file)) // 第二种方法
                .compose(RxUtils.<HttpEntity>applySchedulers())
                .subscribe(new HttpSubscriber<HttpEntity>() {
                    @Override
                    public void succeed(@NonNull HttpEntity pData) {
                        int code = pData.getCode();
                        String msg = pData.getMsg();
                        Logger.i("请求成功 -> msg:" + pData.getMsg());
                    }

                    @Override
                    public void finished() {
                    }
                });
    }
}

