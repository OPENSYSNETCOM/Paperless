package com.opensysnet.paperless.dto;

import java.io.Serializable;

public class WebSocketResponse<T> implements Serializable {
    /**
	 * default serialize
	 */
	private static final long serialVersionUID = 1L;

	private Integer totalCount;
    private String remark;
    private T data;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
