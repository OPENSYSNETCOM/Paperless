package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespWorkingInfo {
    @SerializedName("seq")
    @Expose
    private int seq;
    @SerializedName("organ")
    @Expose
    private String organ;
    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("comtime")
    @Expose
    private String comtime;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getComtime() {
        return comtime;
    }

    public void setComtime(String comtime) {
        this.comtime = comtime;
    }
}
