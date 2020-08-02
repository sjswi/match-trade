/*
 * @project InvoiceException  V1.0
 * @filename: InvoiceException 2020-07-10
 * Copyright(c) 2020 kinbug Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.mt.exception;

/**
 * @author senkyouku
 * @ClassName: ExchangeException
 * @Description: 异常信息
 * @date 2020-07-10
 */
public class ExchangeException extends RuntimeException {

    private int code;

    public ExchangeException(String message) {
        super(message);
    }

    public ExchangeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ExchangeException(ExchangeError err) {
        super(err.getMessage());
        this.code = err.getCode();
    }

    public ExchangeException(Throwable cause) {
        super(cause);
    }

    public ExchangeException(ExchangeError err, Throwable cause) {
        super(err.getMessage(), cause);
        this.code = err.getCode();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
