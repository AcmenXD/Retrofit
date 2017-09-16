package com.acmenxd.retrofit.callback;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.acmenxd.retrofit.HttpEntity;
import com.acmenxd.retrofit.HttpError;
import com.acmenxd.retrofit.HttpGenericityEntity;
import com.acmenxd.retrofit.HttpManager;
import com.acmenxd.retrofit.HttpResultCallback;
import com.acmenxd.retrofit.exception.HttpException;
import com.acmenxd.retrofit.exception.HttpNoDataBodyException;
import com.acmenxd.retrofit.exception.HttpNoDataTypeException;
import com.acmenxd.retrofit.exception.HttpResponseException;

import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/3 15:30
 * @detail 普通方式的网络请求回调
 */
public abstract class HttpCallback<T> implements Callback<T>, IHttpProgress {
    /**
     * 是否自行处理返回的数据 或 非已定义的NetEntity和Bitmap 类型 需设置此变量为true
     * * 如为true, 则onResponse会直接回调succeed, 忽略对错误的处理
     * * 所以在succeed函数中需手动调用NetCode.parseNetCode处理服务器返回的异常
     */
    private boolean isAlreadyOperationData = false;

    public HttpCallback() {

    }

    public HttpCallback(boolean isAlreadyOperationData) {
        this.isAlreadyOperationData = isAlreadyOperationData;
    }

    // ---------------------------- 重写函数 ------------------------

    /**
     * 请求成功回调
     * * 非必须重写,可根据需要自行实现
     */
    public void succeed(@NonNull T pData) {
    }

    /**
     * 请求失败回调
     * * 非必须重写,可根据需要自行实现
     */
    public void succeed(@NonNull Response pResponse, @NonNull T pData) {
    }

    /**
     * 请求失败回调
     * * 非必须重写,可根据需要自行实现
     */
    public void succeed(@NonNull Call pCall, @NonNull Response pResponse, @NonNull T pData) {
    }

    /**
     * 请求失败回调
     * * 非必须重写,可根据需要自行实现
     */
    public void failed(@NonNull HttpException pE) {
    }

    /**
     * 请求完成回调
     * * 非必须重写,可根据需要自行实现
     */
    public void finished() {
    }

    /**
     * 进度回调
     *
     * @param isDone   是否完成
     * @param total    总字节数
     * @param progress 已经下载或上传字节数
     */
    @Override
    public void progress(boolean isDone, long total, long progress) {

    }

    // ---------------------------- 统一处理函数,无需关心 ------------------------
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onResponse2(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure2(call, t);
    }

    public final void onResponse2(@NonNull final Call<T> call, @NonNull final Response<T> response) {
        // 服务器合理响应
        int code = response.raw().code();
        if (code == HttpError.SUCCESS_RESPONSE) {
            final T data = response.body();
            if (isAlreadyOperationData) {
                success(call, response, data);
                return;
            }
            /**
             * 数据处理部分
             */
            if (data != null) {
                // 返回类型BaseEntity
                if (data instanceof HttpEntity || data instanceof HttpGenericityEntity) {
                    // 所有请求成功,下发前统一回调
                    HttpResultCallback resultCallback = HttpManager.INSTANCE.resultCallback;
                    if (resultCallback == null || !resultCallback.success(((HttpEntity) data).getCode(), ((HttpEntity) data).getMsg())) {
                        success(call, response, data);
                    }
                }
                // 返回类型Serializable
                else if (data instanceof Serializable) {
                    success(call, response, data);
                }
                // 返回类型ResponseBody
                else if (data instanceof ResponseBody) {
                    success(call, response, data);
                }
                // 返回类型Bitmap
                else if (data instanceof Bitmap) {
                    success(call, response, data);
                }
                // 返回类型无定义,统一处理为NetNoDataTypeException异常
                else {
                    onFailure(call, new HttpNoDataTypeException("http error -> no type error"));
                }
            }
            // data空,统一处理为解析异常:NetNoDataBodyException
            else {
                onFailure(call, new HttpNoDataBodyException("http error -> response body null error"));
            }
        }
        // 服务器或请求过程没有正常响应,统一处理为响应异常:NetResponseException
        else {
            onFailure(call, new HttpResponseException(code, "http response error : " + response.raw().toString()));
        }
    }

    public final void onFailure2(@NonNull Call<T> call, @NonNull Throwable pE) {
        // 解析后的Exception
        HttpException exception = HttpError.parseException(pE);
        // 所有请求失败,下发前统一回调
        HttpResultCallback resultCallback = HttpManager.INSTANCE.resultCallback;
        if (resultCallback == null || !resultCallback.fail(exception)) {
            fail(exception);
        }
    }

    /**
     * 请求成功
     * * 每个回调方法->成功的 都会调用
     */
    private final void success(@NonNull Call pCall, @NonNull Response pResponse, @NonNull T pData) {
        succeed(pData);
        succeed(pResponse, pData);
        succeed(pCall, pResponse, pData);
        finish();
    }

    /**
     * 请求失败
     * * 每个回调方法->失败的 都会调用
     */
    private final void fail(@NonNull HttpException pE) {
        failed(pE);
        finish();
    }

    /**
     * 请求完成
     * * 每个回调方法->完成的 都会调用
     */
    private final void finish() {
        finished();
    }
}
