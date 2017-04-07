package com.acmenxd.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.acmenxd.retrofit.exception.NetException;
import com.acmenxd.retrofit.exception.NetNoDataBodyException;
import com.acmenxd.retrofit.exception.NetNoDataTypeException;
import com.acmenxd.retrofit.exception.NetResponseException;
import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/5 11:16
 * @detail 网络状态码
 */

public final class NetError {
    public static final String MSG = "未知错误";
    public static final String TOAST_MSG = "网络繁忙,请稍后再试!";
    /**
     * 响应状态码
     */
    public static final int SUCCESS_RESPONSE = 200;// 响应正常

    /**
     * 异常状态码 -> 系统异常
     */
    public static final int ERROR_UNAUTHORIZED = 401; //未授权的请求
    public static final int ERROR_FORBIDDEN = 403; //禁止访问
    public static final int ERROR_NOT_FOUND = 404; //服务器地址未找到
    public static final int ERROR_REQUEST_TIMEOUT = 408; //请求超时
    public static final int ERROR_INTERNAL_SERVER_ERROR = 500; //服务器出错
    public static final int ERROR_BAD_GATEWAY = 502; //无效的请求
    public static final int ERROR_SERVICE_UNAVAILABLE = 503; //服务器不可用
    public static final int ERROR_GATEWAY_TIMEOUT = 504; //网关响应超时
    public static final int ERROR_ACCESS_DENIED = 302; //网络错误
    public static final int ERROR_HANDEL_ERRROR = 417; //接口处理失败

    /**
     * 异常状态码 -> 请求异常
     */
    public static final int ERROR = -10; // 统一异常,找不到已定义的异常时,统一发送此异常
    public static final int ERROR_Http = -100; // 统一的HttpException类型异常
    public static final int ERROR_NO_NETWORK = -101; // 无网络连接
    public static final int ERROR_HOST = -102; // 网络已连接,但无法访问Internet -> 主机IP地址无法确定
    public static final int ERROR_SocketTimeout = -103; // socket连接超时异常
    public static final int ERROR_Connect = -104; // 连接异常
    public static final int ERROR_Parse = -105; // 解析异常
    public static final int ERROR_SSL = -106; // 证书验证失败

    /**
     * 异常状态码 -> 自定义异常
     */
    public static final int ERROR_RESPONSE = -1000; // 响应异常
    public static final int ERROR_NO_DATA_TYPE = -1001; // 数据无匹配type异常
    public static final int ERROR_RESPONSE_BODY = -1001; // 数据解析空异常

    /**
     * 分解异常情况
     */
    public static NetException parseException(Throwable pE) {
        int code = ERROR;
        String msg = MSG;
        String toastMsg = TOAST_MSG;
        /**
         * 检查网络连接
         */
        if (!checkNetWork()) {
            code = ERROR_NO_NETWORK;
            msg = "无网络连接";
            toastMsg = "无网络连接";
        } else if (pE instanceof UnknownHostException) {
            code = ERROR_HOST;
            msg = "网络已连接,但无法访问Internet";
            toastMsg = "网络已连接,但无法访问Internet";
        }
        /**
         * 系统异常
         */
        else if (pE instanceof HttpException) {
            HttpException httpException = (HttpException) pE;
            switch (httpException.code()) {
                case ERROR_UNAUTHORIZED:
                    code = ERROR_UNAUTHORIZED;
                    msg = "未授权的请求";
                    break;
                case ERROR_FORBIDDEN:
                    code = ERROR_FORBIDDEN;
                    msg = "禁止访问";
                    break;
                case ERROR_NOT_FOUND:
                    code = ERROR_NOT_FOUND;
                    msg = "服务器地址未找到";
                    break;
                case ERROR_REQUEST_TIMEOUT:
                    code = ERROR_REQUEST_TIMEOUT;
                    msg = "请求超时";
                    break;
                case ERROR_INTERNAL_SERVER_ERROR:
                    code = ERROR_INTERNAL_SERVER_ERROR;
                    msg = "服务器出错";
                    break;
                case ERROR_BAD_GATEWAY:
                    code = ERROR_BAD_GATEWAY;
                    msg = "无效的请求";
                    break;
                case ERROR_SERVICE_UNAVAILABLE:
                    code = ERROR_SERVICE_UNAVAILABLE;
                    msg = "服务器不可用";
                    break;
                case ERROR_GATEWAY_TIMEOUT:
                    code = ERROR_GATEWAY_TIMEOUT;
                    msg = "网关响应超时";
                    break;
                case ERROR_ACCESS_DENIED:
                    code = ERROR_ACCESS_DENIED;
                    msg = "网络错误";
                    break;
                case ERROR_HANDEL_ERRROR:
                    code = ERROR_HANDEL_ERRROR;
                    msg = "接口处理失败";
                    break;
                default:
                    code = ERROR_Http;
                    msg = "HttpException";
                    break;
            }
        }
        /**
         * 请求异常
         */
        else if (pE instanceof SocketTimeoutException || pE instanceof ConnectTimeoutException) {
            code = ERROR_SocketTimeout;
            msg = "连接超时";
        } else if (pE instanceof ConnectException) {
            code = ERROR_Connect;
            msg = "连接失败";
        } else if (pE instanceof JsonParseException || pE instanceof JSONException || pE instanceof ParseException) {
            code = ERROR_Parse;
            msg = "解析错误";
        } else if (pE instanceof SSLHandshakeException) {
            code = ERROR_SSL;
            msg = "证书验证失败";
        }
        /**
         * 自定义
         */
        else if (pE instanceof NetException) {
            return (NetException) pE;
        } else if (pE instanceof NetResponseException) {
            code = ERROR_RESPONSE;
            msg = "响应异常" + pE.getMessage();
        } else if (pE instanceof NetNoDataTypeException) {
            code = ERROR_NO_DATA_TYPE;
            msg = "数据无匹配type异常" + pE.getMessage();
        } else if (pE instanceof NetNoDataBodyException) {
            code = ERROR_RESPONSE_BODY;
            msg = "数据解析空异常" + pE.getMessage();
        }
        return new NetException(pE, code, msg, toastMsg);
    }

    /**
     * 检查网络是否连接
     */
    private static boolean checkNetWork() {
        boolean result = false;
        Context context = NetManager.INSTANCE.getBuilder().getContext();
        // 网络连接信息
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 进行判断网络是否连接
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    result = true;
                }
            }
        }
        return result;
    }

}
