package com.acmenxd.retrofit.exception;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/3/10 10:42
 * @detail Net数据正常返回
 */
public class NetExceptionSuccess extends NetException {
    public NetExceptionSuccess(int pCode, String pMsg, String pToastMsg) {
        super(pCode, pMsg, pToastMsg);
    }
}
