package com.acmenxd.retrofit;

import com.acmenxd.logger.LogTag;
import com.acmenxd.logger.LogType;
import com.acmenxd.logger.Logger;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/3 18:27
 * @detail 网络日志输出类
 */
public class NetLog {

    /**
     * Net 日志输出
     */
    public static void print(String inStr) {
        NetManager.Builder builder = NetManager.INSTANCE.getBuilder();
        boolean net_log_open = builder.isNet_log_open();
        LogType logType = builder.getNet_log_level();
        LogTag logTag = builder.getNet_log_tag();
        if (!net_log_open) {
            return;
        }
        if (logType == LogType.V) {
            Logger.v(logTag, inStr);
        } else if (logType == LogType.D) {
            Logger.d(logTag, inStr);
        } else if (logType == LogType.I) {
            Logger.i(logTag, inStr);
        } else if (logType == LogType.W) {
            Logger.w(logTag, inStr);
        } else if (logType == LogType.E) {
            Logger.e(logTag, inStr);
        } else if (logType == LogType.A) {
            Logger.a(logTag, inStr);
        } else if (logType == LogType.JSON) {
            Logger.json(logTag, inStr);
        } else if (logType == LogType.XML) {
            Logger.xml(logTag, inStr);
        } else if (logType == LogType.FILE) {
            Logger.file(logTag, inStr);
        }
    }
}
