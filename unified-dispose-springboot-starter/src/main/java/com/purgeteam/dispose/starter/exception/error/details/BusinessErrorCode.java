package com.purgeteam.dispose.starter.exception.error.details;

/**
 * 业务通用异常枚举
 *
 * @author purgeyao
 * @since 1.0
 */
public enum BusinessErrorCode {

    /**
     * 通用业务异常
     */
    BUSINESS_ERROR("CLOUD800", "业务异常"),
    ;

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    BusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
