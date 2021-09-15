package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespInstallInfoSam {
    @SerializedName("tmoney_1")
    @Expose
    private String tmoney1;
    @SerializedName("tmoney_2")
    @Expose
    private String tmoney2;
    @SerializedName("intsam_1")
    @Expose
    private String intsam1;
    @SerializedName("intsam_2")
    @Expose
    private String intsam2;

    public String getTmoney1() {
        return tmoney1;
    }

    public void setTmoney1(String tmoney1) {
        this.tmoney1 = tmoney1;
    }

    public String getTmoney2() {
        return tmoney2;
    }

    public void setTmoney2(String tmoney2) {
        this.tmoney2 = tmoney2;
    }

    public String getIntsam1() {
        return intsam1;
    }

    public void setIntsam1(String intsam1) {
        this.intsam1 = intsam1;
    }

    public String getIntsam2() {
        return intsam2;
    }

    public void setIntsam2(String intsam2) {
        this.intsam2 = intsam2;
    }
}
