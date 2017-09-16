package com.acmenxd.retrofit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.acmenxd.retrofit.HttpManager;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/11/22 14:36
 * @detail 工具类
 */
public class RetrofitUtils {

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 串拼接
     *
     * @param strs 可变参数类型
     * @return 拼接后的字符串
     */
    public static String appendStrs(Object... strs) {
        StringBuilder sb = new StringBuilder();
        if (strs != null && strs.length > 0) {
            for (Object str : strs) {
                sb.append(String.valueOf(str));
            }
        }
        return sb.toString();
    }

    /**
     * 检查网络是否连接
     */
    public static boolean checkNetwork() {
        if (HttpManager.INSTANCE.context == null) {
            return false;
        }
        boolean result = false;
        ConnectivityManager connMgr = (ConnectivityManager) HttpManager.INSTANCE.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return result;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
                result = true;
            } else {
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (dataNetworkInfo != null && dataNetworkInfo.isConnected()) {
                    result = true;
                }
            }
        } else {
            //API大于23时使用下面的方式进行网络监听
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            if (networks != null && networks.length > 0) {
                for (int i = 0, len = networks.length; i < len; i++) {
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                    if (networkInfo != null && networkInfo.isConnected()) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
