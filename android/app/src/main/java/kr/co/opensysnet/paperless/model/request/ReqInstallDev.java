package kr.co.opensysnet.paperless.model.request;

public class ReqInstallDev {
    private String dev_name;
    private int dev_order;
    private String model_name;
    private String asset_code;
    private String dev_type;
    private String status;
    private String description;

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public int getDev_order() {
        return dev_order;
    }

    public void setDev_order(int dev_order) {
        this.dev_order = dev_order;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
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
