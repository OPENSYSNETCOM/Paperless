package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespInstallInfoDev {
    @SerializedName("dev_name")
    @Expose
    private String devName;
    @SerializedName("dev_order")
    @Expose
    private String devOrder;
    @SerializedName("model_name")
    @Expose
    private String modelName;
    @SerializedName("asset_code")
    @Expose
    private String assetCode;
    @SerializedName("dev_type")
    @Expose
    private String devType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("description")
    @Expose
    private String description;

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

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
