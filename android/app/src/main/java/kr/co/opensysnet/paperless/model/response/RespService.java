package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespService<T> {
    @SerializedName("resultCode")
    @Expose
    private int resultCode;
    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;
    @SerializedName("service_info")
    @Expose
    private T serviceInfo;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(T serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
