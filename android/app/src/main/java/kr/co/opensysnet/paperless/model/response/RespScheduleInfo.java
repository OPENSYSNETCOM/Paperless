package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespScheduleInfo {
    @SerializedName("seq")
    @Expose
    private int seq;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("organ")
    @Expose
    private String organ;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("ce")
    @Expose
    private String ce;
    @SerializedName("visittime")
    @Expose
    private String visittime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("workdivision")
    @Expose
    private String workdivision;
    @SerializedName("paperless_flag")
    @Expose
    private String paperlessFlag;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getCe() {
        return ce;
    }

    public void setCe(String ce) {
        this.ce = ce;
    }

    public String getVisittime() {
        return visittime;
    }

    public void setVisittime(String visittime) {
        this.visittime = visittime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWorkdivision() {
        return workdivision;
    }

    public void setWorkdivision(String workdivision) {
        this.workdivision = workdivision;
    }

    public String getPaperlessFlag() {
        return paperlessFlag;
    }

    public void setPaperlessFlag(String paperlessFlag) {
        this.paperlessFlag = paperlessFlag;
    }
}
