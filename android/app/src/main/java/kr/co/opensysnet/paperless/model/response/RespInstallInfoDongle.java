package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespInstallInfoDongle {
    @SerializedName("lsam_name")
    @Expose
    private String lsamName;
    @SerializedName("lsam_flag")
    @Expose
    private String lsamFlag;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("cr_test")
    @Expose
    private String cr_test;
    @SerializedName("pay_test")
    @Expose
    private String pay_test;

    public String getLsamName() {
        return lsamName;
    }

    public void setLsamName(String lsamName) {
        this.lsamName = lsamName;
    }

    public String getLsamFlag() {
        return lsamFlag;
    }

    public void setLsamFlag(String lsamFlag) {
        this.lsamFlag = lsamFlag;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCr_test() {
        return cr_test;
    }

    public void setCr_test(String cr_test) {
        this.cr_test = cr_test;
    }

    public String getPay_test() {
        return pay_test;
    }

    public void setPay_test(String pay_test) {
        this.pay_test = pay_test;
    }
}
