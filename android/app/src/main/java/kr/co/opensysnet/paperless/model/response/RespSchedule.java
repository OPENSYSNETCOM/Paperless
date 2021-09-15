package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespSchedule<T> {
    @SerializedName("resultCode")
    @Expose
    private int resultCode;
    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;
    @SerializedName("schedule_cnt")
    @Expose
    private int scheduleCnt;
    @SerializedName("schedule_info")
    @Expose
    private List<T> scheduleInfo;

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

    public int getScheduleCnt() {
        return scheduleCnt;
    }

    public void setScheduleCnt(int scheduleCnt) {
        this.scheduleCnt = scheduleCnt;
    }

    public List<T> getScheduleInfo() {
        return scheduleInfo;
    }

    public void setScheduleInfo(List<T> scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }
}
