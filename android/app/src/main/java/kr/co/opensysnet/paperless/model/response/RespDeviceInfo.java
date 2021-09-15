package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespDeviceInfo<T> {
    @SerializedName("dev_name")
    @Expose
    private String devName;
    @SerializedName("dev_order")
    @Expose
    private int devOrder;
    @SerializedName("model_list")
    @Expose
    private List<T> modelList;

    private List<RespCheckInfoCheck> checkList;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public int getDevOrder() {
        return devOrder;
    }

    public void setDevOrder(int devOrder) {
        this.devOrder = devOrder;
    }

    public List<T> getModelList() {
        return modelList;
    }

    public void setModelList(List<T> modelList) {
        this.modelList = modelList;
    }

    public List<RespCheckInfoCheck> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<RespCheckInfoCheck> checkList) {
        this.checkList = checkList;
    }
}
