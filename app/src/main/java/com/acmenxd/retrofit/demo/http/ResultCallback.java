package com.acmenxd.retrofit.demo.http;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.acmenxd.retrofit.HttpResultCallback;
import com.acmenxd.retrofit.demo.BaseApplication;
import com.acmenxd.retrofit.exception.HttpException;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/3/10 9:50
 * @detail 所有请求返回时, 统一回调
 */
public final class ResultCallback extends HttpResultCallback {
    /**
     * 服务器返回的状态码 - 成功
     */
    public static final int SUCCESS = 0; // 数据正常
    /**
     * 服务器返回的状态码 - 失败
     */
    public static final int ERROR = -1; // 数据异常

    @Override
    public boolean success(int code, @NonNull String msg) {
        if (code == ERROR) {
            Toast.makeText(BaseApplication.instance(), msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public boolean fail(HttpException exception) {
        Toast.makeText(BaseApplication.instance(), exception.getMsg(), Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 解析code码
     */
    public void parseCode(int code, String msg) {

    }
}
