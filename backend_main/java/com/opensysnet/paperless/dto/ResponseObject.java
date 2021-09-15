package com.opensysnet.paperless.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ResponseObject<T> implements Serializable {

    /**
	 * default serialize
	 */
	private static final long serialVersionUID = 1L;

	private ResultCode resultCode = ResultCode.HS200_SUCCESS;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalCount;
    private T data;

    public ResponseObject() {}

    public ResponseObject(T data) {
        this.data = data;
    }
    public T getData() {
        return data;
    }

    public String getMessage() {
        return (message == null) ? resultCode.name() : message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    @JsonProperty("resultCode")
    public Integer getResultCodeValue() {
        return resultCode.getCode();
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty("resultCode")
    public void setResultCodeValue(Integer resultCode) {
        setResultCode(ResultCode.get(resultCode));
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
