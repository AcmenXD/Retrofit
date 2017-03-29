package com.acmenxd.retrofit.exception;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/3 17:01
 * @detail Net数据解析异常
 */
public class NetNoDataBodyException extends Exception {
    public NetNoDataBodyException(String errerStr){
        super(errerStr);
    }
}
