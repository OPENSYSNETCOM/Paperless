package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespServiceInfoDev {
    @SerializedName("dev_name")
    @Expose
    private String devName;
    @SerializedName("dev_order")
    @Expose
    private String devOrder;
    @SerializedName("model_name")
    @Expose
    private String modelName;
    @SerializedName("dev_num")
    @Expose
    private int devNum;

    private RespServiceInfoFix fix;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevOrder() {
        return devOrder;
    }

    public void setDevOrder(String devOrder) {
        this.devOrder = devOrder;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public RespServiceInfoFix getFix() {
        return fix;
    }

    public void setFix(RespServiceInfoFix fix) {
        this.fix = fix;
    }
}
