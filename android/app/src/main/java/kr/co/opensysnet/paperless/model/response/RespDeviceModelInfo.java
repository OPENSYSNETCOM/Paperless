package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespDeviceModelInfo {
    @SerializedName("model_name")
    @Expose
    private String modelName;
    @SerializedName("model_order")
    @Expose
    private int modelOrder;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelOrder() {
        return modelOrder;
    }

    public void setModelOrder(int modelOrder) {
        this.modelOrder = modelOrder;
    }
}
