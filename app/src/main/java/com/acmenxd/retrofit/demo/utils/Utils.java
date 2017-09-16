package com.acmenxd.retrofit.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/11/22 14:36
 * @detail 工具类
 */
public class Utils {

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
     * 根据手机的分辨率从 dp 的单位转成 px(像素)
     */
    public static float dp2px(Context pContext, float dp) {
        return dp2px(pContext.getResources(), dp);
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素)的单位转成 dp
     */
    public static float px2dp(Context pContext, float px) {
        return px2dp(pContext.getResources(), px);
    }

    public static float px2dp(Resources resources, float px) {
        final float scale = resources.getDisplayMetrics().density;
        return px / scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 sp 的单位转成 px(像素)
     */
    public static float sp2px(Context pContext, float sp) {
        return sp2px(pContext.getResources(), sp);
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素)的单位转成 sp
     */
    public static float px2sp(Context pContext, float px) {
        return px2sp(pContext.getResources(), px);
    }

    public static float px2sp(Resources resources, float px) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return px / scale + 0.5f;
    }

    //调用文件选择软件来选择文件 -> 返回文件路径  ---------------start
    public static final int showFileChooser_requestCode = 0x0001; //requestCode状体码

    public static void showFileChooser(Activity pActivity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            pActivity.startActivityForResult(Intent.createChooser(intent, "请选择要上传的文件"), showFileChooser_requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(pActivity.getApplicationContext(), "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    public static String showFileChooser_onActivityResult(Activity pActivity, int requestCode, int resultCode, Intent data) {
        if (requestCode == showFileChooser_requestCode && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = "";
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {"_data"};
                Cursor cursor = null;
                try {
                    cursor = pActivity.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column_index);
                    }
                } catch (Exception e) {
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
            }
            return path;
        }
        return "";
    }
    //调用文件选择软件来选择文件 ---------------end

}
