package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespWorking<T> {
    @SerializedName("resultCode")
    @Expose
    private int resultCode;
    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;
    @SerializedName("working_cnt")
    @Expose
    private int workingCnt;
    @SerializedName("working_info")
    @Expose
    private List<T> workingInfo;

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

    public int getWorkingCnt() {
        return workingCnt;
    }

    public void setWorkingCnt(int workingCnt) {
        this.workingCnt = workingCnt;
    }

    public List<T> getWorkingInfo() {
        return workingInfo;
    }

    public void setWorkingInfo(List<T> workingInfo) {
        this.workingInfo = workingInfo;
    }
}
