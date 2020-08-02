package com.flying.cattle.mt.exception;

/**
 * @author senkyouku
 * @date 2020/07/10
 */
public enum ExchangeError {

    // 异常信息
    Redis(1550, "Exchange redis异常"),
    Http(1540, "Exchange http异常"),
    Rpc(1530, "Exchange rpc异常"),
    RateLimit(1520, "Exchange 流量限制"),
    Parser(1510, "Exchange 解析异常"),
    Unknown(1500, "Exchange 系统异常"),
    Api(1600, "Exchange api错误[retcode:%s,retmsg:%s]"),
    Arg(1700, "Exchange 参数错误:%s");

    private int    code;
    private String message;

    ExchangeError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
