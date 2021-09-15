package com.opensysnet.paperless.dto;

import org.springframework.http.HttpStatus;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ResultCode {

    HS200_SUCCESS(200, HttpStatus.OK,        "SUCCESS"),
    HS400_BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "This is invalid request."),
    HS401_UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "unauthenticated user"),
    HS402_DUPLICATED_EXCEPTION(400_00_02, HttpStatus.BAD_REQUEST, "There is conflict data."),
    HS403_FORBIDDEN(403, HttpStatus.FORBIDDEN, "forbidden access"),
    HS404_NOT_FOUND(404, HttpStatus.NOT_FOUND, "not found"),
    HS500_EXCEPTION(500, HttpStatus.INTERNAL_SERVER_ERROR, "unknown error"),


    HS1000_INVALID_PARAMETER(1000, HttpStatus.BAD_REQUEST, "There are invalid parameters."),
    HS1001_NOT_REGISTERED_ID(1001, HttpStatus.UNAUTHORIZED, "Not Registered User ID."),
    HS1002_INVALID_PWD(1001, HttpStatus.UNAUTHORIZED, "Invalid Password");


    private int code;
    private HttpStatus httpStatus;
    private String defaultMessage;
    private static final Map<Integer, ResultCode> lookup =
            new ConcurrentHashMap<Integer, ResultCode>();

    static {
        for (ResultCode aCode : EnumSet.allOf(ResultCode.class)) {
            lookup.put(aCode.code, aCode);
        }
    }

    ResultCode(int code, HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public static ResultCode get(int code) {
        return lookup.get(code);
    }

    public int getCode() {
        return this.code;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}
