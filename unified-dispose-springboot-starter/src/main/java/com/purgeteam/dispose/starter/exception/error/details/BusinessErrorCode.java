package com.purgeteam.dispose.starter.exception.error.details;

import lombok.Getter;

/**
 * 业务通用异常枚举
 *
 * @author purgeyao
 * @since 1.0
 */
@Getter
public enum BusinessErrorCode {

    /**
     * 通用业务异常
     */
    BUSINESS_ERROR("CLOUD800","业务异常"),
    ;

    private String code;

    private String message;

    BusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
