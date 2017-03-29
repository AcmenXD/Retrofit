package com.acmenxd.retrofit.exception;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/3/10 10:42
 * @detail Net数据异常返回
 */
public class NetExceptionFail extends NetException {
    public NetExceptionFail(int pCode, String pMsg, String pToastMsg) {
        super(pCode, pMsg, pToastMsg);
    }
}
